package com.visualvault.visual_vault_project.dto;

import java.util.List;

public record VideoCreateDTO(
                String titulo,
                String descripcion,
                String url,
                String plataforma,
                String miniaturaUrl,
                Boolean visto,
                String categoria,
                List<String> etiquetas,
                Long usuarioId) {

}
