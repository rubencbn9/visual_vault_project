package com.visualvault.visual_vault_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria ,Long>{
    
    Optional<Categoria> findByNombre(String nombre);
}
