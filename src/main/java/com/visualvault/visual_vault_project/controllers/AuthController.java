package com.visualvault.visual_vault_project.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visualvault.visual_vault_project.dto.AuthResponseDTO;
import com.visualvault.visual_vault_project.dto.ChangePasswordRequestDTO;
import com.visualvault.visual_vault_project.dto.LoginRequestDTO;
import com.visualvault.visual_vault_project.dto.RegisterRequestDTO;
import com.visualvault.visual_vault_project.entity.JwtUtil;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500" })
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ----------------------------------------------------
    // REGISTRO
    // ----------------------------------------------------
    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea un nuevo usuario verificando que username y email no existan previamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existente", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", 
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            Optional<Usuario> existingUser = usuarioRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("El usuario ya existe");
            }

            Optional<Usuario> existingEmail = usuarioRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            Usuario usuario = Usuario.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .contrasenaHash(passwordEncoder.encode(request.getPassword()))
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            Usuario savedUsuario = usuarioRepository.save(usuario);

            String token = jwtUtil.generateToken(savedUsuario.getUsername());

            return ResponseEntity.ok(new AuthResponseDTO(token, savedUsuario.getUsername()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar usuario: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // LOGIN
    // ----------------------------------------------------
    @Operation(summary = "Iniciar sesión",
               description = "Valida las credenciales del usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
                return ResponseEntity.badRequest().body("Contraseña incorrecta");
            }

            String token = jwtUtil.generateToken(usuario.getUsername());

            return ResponseEntity.ok(new AuthResponseDTO(token, usuario.getUsername()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al iniciar sesión: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // DATOS DEL USUARIO LOGUEADO
    // ----------------------------------------------------
    @Operation(summary = "Obtener usuario logueado",
               description = "Devuelve la información del usuario asociada al token enviado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            return ResponseEntity.ok(usuarioOpt.get());

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // CAMBIAR CONTRASEÑA
    // ----------------------------------------------------
    @Operation(summary = "Cambiar contraseña",
               description = "Permite al usuario cambiar su contraseña verificando la actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada correctamente",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o contraseña actual incorrecta",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getContrasenaHash())) {
                return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
            }

            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body("La nueva contraseña debe tener al menos 6 caracteres");
            }

            usuario.setContrasenaHash(passwordEncoder.encode(request.getNewPassword()));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Contraseña cambiada correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cambiar contraseña: " + e.getMessage());
        }
    }
}
