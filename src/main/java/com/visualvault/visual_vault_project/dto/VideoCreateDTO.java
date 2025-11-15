package com.visualvault.visual_vault_project.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VideoCreateDTO(
                String titulo,
    String descripcion,
    String url,
    String fuente,
    String miniaturaUrl,
    Boolean visto,
    String categoria,
    List<String> etiquetas,
    Long usuarioId,
    LocalDateTime fechaGuardado) {

}
