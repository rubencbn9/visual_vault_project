package com.visualvault.visual_vault_project.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.visualvault.visual_vault_project.entity.Rol;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador de administración (God Mode)
 * Solo accesible para usuarios con rol ADMINISTRADOR
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Admin", description = "Endpoints de administración (solo ADMINISTRADOR)")
public class AdminController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ----------------------------------------------------
    // ESTADÍSTICAS GENERALES
    // ----------------------------------------------------
    @Operation(summary = "Obtener estadísticas", description = "Devuelve el conteo total de usuarios y videos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("totalVideos", videoRepository.count());
        stats.put("usuariosPorRol", Map.of(
                "USUARIO", usuarioRepository.findAll().stream().filter(u -> u.getRol() == Rol.USUARIO).count(),
                "ADMINISTRADOR",
                usuarioRepository.findAll().stream().filter(u -> u.getRol() == Rol.ADMINISTRADOR).count()));
        return ResponseEntity.ok(stats);
    }

    // ----------------------------------------------------
    // LISTAR TODOS LOS USUARIOS CON DETALLES
    // ----------------------------------------------------
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve todos los usuarios con su número de videos")
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> usersWithDetails = usuarioRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getIdUsuario());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("rol", user.getRol().name());
                    userMap.put("fechaRegistro", user.getFechaRegistro());
                    userMap.put("profilePicture", user.getProfilePicture());
                    userMap.put("totalVideos", user.getVideos() != null ? user.getVideos().size() : 0);
                    return userMap;
                })
                .toList();
        return ResponseEntity.ok(usersWithDetails);
    }

    // ----------------------------------------------------
    // ELIMINAR CUALQUIER USUARIO
    // ----------------------------------------------------
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario y todos sus datos asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------
    // CREAR USUARIO CON ROL ESPECÍFICO
    // ----------------------------------------------------
    @Operation(summary = "Crear usuario", description = "Crea un usuario con un rol específico (USUARIO o ADMINISTRADOR)")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        // Verificar si el username ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El username ya existe");
        }

        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .contrasenaHash(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol() != null ? request.getRol() : Rol.USUARIO)
                .build();
        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------
    // LISTAR TODOS LOS VIDEOS
    // ----------------------------------------------------
    @Operation(summary = "Listar todos los videos", description = "Devuelve todos los videos del sistema")
    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoRepository.findAll());
    }

    // ----------------------------------------------------
    // ELIMINAR CUALQUIER VIDEO
    // ----------------------------------------------------
    @Operation(summary = "Eliminar video", description = "Elimina cualquier video del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Video eliminado"),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content)
    })
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------
    // CAMBIAR ROL DE USUARIO
    // ----------------------------------------------------
    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol de un usuario existente")
    @PostMapping("/users/{id}/change-role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestBody ChangeRoleRequest request) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setRol(request.getRol());
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //------------------------------------------------------
    // DTO INTERNO PARA CRAR USUARIO
    //------------------------------------------------------

    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private Rol rol;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Rol getRol() {
            return rol;
        }

        public void setRol(Rol rol) {
            this.rol = rol;
        }
    }

    //------------------------------------------------------
    // DTO INTERNO PARA CAMBIAR ROL
    //------------------------------------------------------

    public static class ChangeRoleRequest {
        private Rol rol;

        public Rol getRol() {
            return rol;
        }

        public void setRol(Rol rol) {
            this.rol = rol;
        }
    }
}