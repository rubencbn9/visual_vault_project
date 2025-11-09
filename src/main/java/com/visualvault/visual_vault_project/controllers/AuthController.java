package com.visualvault.visual_vault_project.controllers;

import java.time.LocalDateTime;

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
        // Verificar si el usuario ya existe
        Usuario existingUser = usuarioRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .contrasenaHash(passwordEncoder.encode(request.getPassword()))
                .fechaRegistro(LocalDateTime.now())
                .build();
        
        usuarioRepository.save(usuario);
        
        // Generar token
        String token = jwtUtil.generateToken(usuario.getUsername());
        
        // Guardar también el username como "nombre" para el frontend
        return ResponseEntity.ok(new AuthResponseDTO(token, usuario.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername());
        
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
            return ResponseEntity.badRequest().body("Contraseña incorrecta");
        }

        // Generar token
        String token = jwtUtil.generateToken(usuario.getUsername());
        
        return ResponseEntity.ok(new AuthResponseDTO(token, usuario.getUsername()));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
            String username = jwtUtil.extractUsername(token);
            
            Usuario usuario = usuarioRepository.findByUsername(username);
            
            if (usuario == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}