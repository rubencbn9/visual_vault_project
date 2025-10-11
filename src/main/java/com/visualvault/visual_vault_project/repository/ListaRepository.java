package com.visualvault.visual_vault_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Lista;

public interface ListaRepository extends JpaRepository<Lista ,Long>{
    
    List<Lista> findByUsuario_IdUsuario(Long idUsuario);
}
