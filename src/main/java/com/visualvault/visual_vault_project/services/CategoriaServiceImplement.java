package com.visualvault.visual_vault_project.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.visualvault.visual_vault_project.entity.Categoria;
import com.visualvault.visual_vault_project.repository.CategoriaRepository;

@Service
public class CategoriaServiceImplement implements CategoriaService {

    @Autowired
    CategoriaRepository categoriaRepository;


   public List<Categoria> obtenerCategorias(){
        return categoriaRepository.findAll();
    };

    public Categoria crearcategoria(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> obtenercategoriaPorId(Long id){
        return categoriaRepository.findById(id);
    }

    public void deletecategoria(Long id){
        categoriaRepository.deleteById(id);
    }
}
