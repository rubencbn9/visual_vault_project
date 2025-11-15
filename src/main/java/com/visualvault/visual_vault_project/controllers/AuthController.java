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
import com.visualvault.visual_vault_project.dto.LoginRequestDTO;
import com.visualvault.visual_vault_project.dto.RegisterRequestDTO;
import com.visualvault.visual_vault_project.entity.JwtUtil;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            // Verificar si el usuario ya existe
            Optional<Usuario> existingUser = usuarioRepository.findByUsername(request.getUsername());
            
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("El usuario ya existe");
            }
            
            // Verificar si el email ya existe
            Optional<Usuario> existingEmail = usuarioRepository.findByEmail(request.getEmail());
            
            if (existingEmail.isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            // Crear nuevo usuario
            Usuario usuario = Usuario.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .contrasenaHash(passwordEncoder.encode(request.getPassword()))
                    .fechaRegistro(LocalDateTime.now())
                    .build();
            
            Usuario savedUsuario = usuarioRepository.save(usuario);
            
            // Generar token
            String token = jwtUtil.generateToken(savedUsuario.getUsername());
            
            return ResponseEntity.ok(new AuthResponseDTO(token, savedUsuario.getUsername()));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            // Buscar usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());
            
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();

            // Verificar contraseña
            if (!passwordEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
                return ResponseEntity.badRequest().body("Contraseña incorrecta");
            }

            // Generar token
            String token = jwtUtil.generateToken(usuario.getUsername());
            
            return ResponseEntity.ok(new AuthResponseDTO(token, usuario.getUsername()));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al iniciar sesión: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
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
}