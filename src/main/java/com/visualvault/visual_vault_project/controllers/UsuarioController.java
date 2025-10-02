package com.visualvault.visual_vault_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import com.visualvault.visual_vault_project.services.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

     @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    //LISTAR USUARIOS
   @GetMapping("/list")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    
    // LISTAR USUARIOS POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findByIdentificacion(id);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //CREAR USUARIOS
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.añadir(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }


    //ACTUALIZAR USUARIOS    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.editar( usuario);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //ELIMINAR USUARIOS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.borrar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
