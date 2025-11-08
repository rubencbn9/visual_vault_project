package com.visualvault.visual_vault_project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.visualvault.visual_vault_project.entity.Categoria;


public record VideoResponseDTO(Long id,
    String titulo,
    String descripcion,
    String url,
    String miniaturaUrl,
    String fuente,
    Boolean visto,
    String categoria,
    List<String> etiquetas,
    LocalDateTime fechaGuardado,
    String usuarioNombre) {
    
}
