package com.visualvault.visual_vault_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    
    Usuario findByNombre (String nombre);
    Usuario findByIdentificacion(Long id);
    List<Usuario> findByNombreContainingIgnoreCase (String nombre);
}
