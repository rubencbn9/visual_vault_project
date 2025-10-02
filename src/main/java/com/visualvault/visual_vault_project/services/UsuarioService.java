package com.visualvault.visual_vault_project.services;

import java.util.List;

import com.visualvault.visual_vault_project.entity.Usuario;

public interface UsuarioService {

     List<Usuario> obtenerTodos();

     Usuario añadir(Usuario usuario);

     Usuario editar(Usuario usuario);

     boolean borrar(Long id);

     List<Usuario> buscarPorNombre(String textoNombre);

     Usuario obtenerPorId(Long id);


     void updateUsuario(Usuario usuario);

     Usuario getUsuarioByNombre(String nombre);
     

}
