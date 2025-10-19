package com.visualvault.visual_vault_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta ,Long>{
    List<Etiqueta> findByNombre(String nombre);
}
