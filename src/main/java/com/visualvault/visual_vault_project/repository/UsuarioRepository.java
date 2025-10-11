package com.visualvault.visual_vault_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    
    Usuario findByUsername (String username);
    Usuario findByIdUsuario(Long idUsuario);
    List<Usuario> findByUsernameContainingIgnoreCase (String username);
}
