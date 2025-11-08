package com.visualvault.visual_vault_project.dto;

import java.util.List;

public record ListaResponseDTO(Long idLista,
                    String nombre,
                    List<VideoResponseDTO> videos            
    ) {
    
}
