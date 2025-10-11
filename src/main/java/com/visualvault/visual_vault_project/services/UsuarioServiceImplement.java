package com.visualvault.visual_vault_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;

@Service
public class UsuarioServiceImplement implements UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = usuarioRepository.findAll();
        return lista;
    }

    public Usuario añadir(Usuario usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()) != null)
            return null;
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Usuario editar(Usuario usuario) {
        Usuario usuarioEncontrado = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
        if (usuarioEncontrado == null) {
            return null; // Retorna null si no se encuentra el usuario
        }

        // Actualiza los campos del usuario encontrado con los datos del formulario
        usuarioEncontrado.setUsername(usuario.getUsername());
        usuarioEncontrado.setEmail(usuario.getEmail());

        // Guarda el usuario actualizado en la base de datos
        return usuarioRepository.save(usuarioEncontrado);
    }

    public boolean borrar(Long id) {
        Usuario usuario = obtenerPorId(id);
        if (usuario == null) {
            return false;
        }
        usuarioRepository.delete(usuario);
        return true;
    }

    public List<Usuario> buscarPorNombre(String textoNombre) {
        textoNombre = textoNombre.toLowerCase();
        List<Usuario> encontrados = usuarioRepository.findByUsernameContainingIgnoreCase(textoNombre);

        return encontrados;
    }

    public Usuario obtenerPorId(Long id) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getIdUsuario() == id) {

                return usuario;
            }
        }
        throw new RuntimeException();
    }

    public void updateUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioByNombre(String nombre) {
        return usuarioRepository.findByUsername(nombre);
    }

}
