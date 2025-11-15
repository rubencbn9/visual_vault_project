package com.visualvault.visual_vault_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    
    Optional<Usuario> findByUsername (String username);
    Usuario findByIdUsuario(Long idUsuario);
    List<Usuario> findByUsernameContainingIgnoreCase (String username);
    Optional<Usuario> findByEmail (String email);

}
