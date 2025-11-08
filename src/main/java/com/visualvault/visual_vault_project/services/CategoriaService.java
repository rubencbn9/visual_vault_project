package com.visualvault.visual_vault_project.services;

import java.util.List;
import java.util.Optional;

import com.visualvault.visual_vault_project.entity.Categoria;

public interface CategoriaService {
    List<Categoria> obtenerCategorias();

    Categoria crearcategoria(Categoria categoria);

    Optional<Categoria> obtenercategoriaPorId(Long id);
    
    void deletecategoria(Long id);
}
