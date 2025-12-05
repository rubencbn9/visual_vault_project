package com.visualvault.visual_vault_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import com.visualvault.visual_vault_project.services.FileStorageService;
import com.visualvault.visual_vault_project.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ----------------------------------------------------
    // LISTAR USUARIOS
    // ----------------------------------------------------
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista completa de usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/list")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // ----------------------------------------------------
    // OBTENER USUARIO POR ID
    // ----------------------------------------------------
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve los datos de un usuario específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findByIdUsuario(id);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ----------------------------------------------------
    // CREAR USUARIO
    // ----------------------------------------------------
    @Operation(summary = "Crear un nuevo usuario", description = "Permite registrar un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.añadir(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // ----------------------------------------------------
    // ACTUALIZAR USUARIO
    // ----------------------------------------------------
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un usuario existente mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.editar(usuario);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ----------------------------------------------------
    // ELIMINAR USUARIO POR ID
    // ----------------------------------------------------
    @Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario existente según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.borrar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ----------------------------------------------------
    // ELIMINAR USUARIO POR USERNAME
    // ----------------------------------------------------
    @Operation(summary = "Eliminar usuario por username", description = "Elimina un usuario mediante su nombre de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> borrarUsuarioPorUsername(@PathVariable String username) {
        boolean eliminado = usuarioService.borrarPorUsername(username);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ----------------------------------------------------
    // SUBIR FOTO DE PERFIL
    // ----------------------------------------------------
    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Subir foto de perfil", description = "Permite subir o actualizar la foto de perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto de perfil actualizada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Archivo inválido", content = @Content)
    })
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Obtener usuario actual
            Usuario usuario = usuarioRepository.findByIdUsuario(id);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Si ya tiene una foto de perfil, eliminar la anterior
            if (usuario.getProfilePicture() != null) {
                try {
                    fileStorageService.delete(usuario.getProfilePicture());
                } catch (Exception e) {
                    // Ignorar si el archivo anterior no existe
                }
            }

            // Guardar nuevo archivo
            String filename = fileStorageService.store(file);

            // Actualizar usuario con nueva foto
            Usuario updatedUser = usuarioService.updateProfilePicture(id, filename);

            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ----------------------------------------------------
    // OBTENER FOTO DE PERFIL
    // ----------------------------------------------------
    @Operation(summary = "Obtener foto de perfil", description = "Devuelve la foto de perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto de perfil encontrada"),
            @ApiResponse(responseCode = "404", description = "Usuario o foto no encontrada", content = @Content)
    })
    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findByIdUsuario(id);
            if (usuario == null || usuario.getProfilePicture() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = fileStorageService.loadAsResource(usuario.getProfilePicture());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + usuario.getProfilePicture() + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ----------------------------------------------------
    // ELIMINAR FOTO DE PERFIL
    // ----------------------------------------------------
    @Operation(summary = "Eliminar foto de perfil", description = "Elimina la foto de perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto de perfil eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<Void> deleteProfilePicture(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findByIdUsuario(id);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (usuario.getProfilePicture() != null) {
                try {
                    fileStorageService.delete(usuario.getProfilePicture());
                } catch (Exception e) {
                    // Ignorar si el archivo no existe
                }
                usuario.setProfilePicture(null);
                usuarioRepository.save(usuario);
            }

            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
