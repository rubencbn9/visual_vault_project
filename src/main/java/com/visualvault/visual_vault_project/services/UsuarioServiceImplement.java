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

    //------------------------------------------------------
    // LISTAR TODOS LOS USUARIOS
    //------------------------------------------------------
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = usuarioRepository.findAll();
        return lista;
    }

    //------------------------------------------------------
    // AÑADIR USUARIOS
    //------------------------------------------------------

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

    //------------------------------------------------------
    // EDITAR USUARIO
    //------------------------------------------------------
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

    //------------------------------------------------------
    // ELIMINAR USUARIO POR ID
    //------------------------------------------------------
    public boolean borrar(Long id) {
        Usuario usuario = obtenerPorId(id);
        if (usuario == null) {
            return false;
        }
        usuarioRepository.delete(usuario);
        return true;
    }

    //------------------------------------------------------
    //BUSCAR USUARIO POR NOMBRE
    //------------------------------------------------------
    public List<Usuario> buscarPorNombre(String textoNombre) {
        textoNombre = textoNombre.toLowerCase();
        List<Usuario> encontrados = usuarioRepository.findByUsernameContainingIgnoreCase(textoNombre);

        return encontrados;
    }

    //------------------------------------------------------
    // OBTENER USUARIO POR ID
    //------------------------------------------------------
    public Usuario obtenerPorId(Long id) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getIdUsuario() == id) {

                return usuario;
            }
        }
        throw new RuntimeException();
    }

    //------------------------------------------------------
    // ACTUALIZAR USUARIO
    //------------------------------------------------------
    public void updateUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    //------------------------------------------------------
    //OBTENER USUARIO POR NOMBRE
    //------------------------------------------------------
    public Usuario getUsuarioByNombre(String nombre) {
        return usuarioRepository.findByUsername(nombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    //------------------------------------------------------
    // BORRAR USUARIO POR NOMBRE
    //------------------------------------------------------
    public boolean borrarPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
        return true;
    }

    //------------------------------------------------------
    // ACTUALIZAR FOTO DE PERFIL USUARIO
    //------------------------------------------------------
    public Usuario updateProfilePicture(Long userId, String filename) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setProfilePicture(filename);
        return usuarioRepository.save(usuario);
    }

}
