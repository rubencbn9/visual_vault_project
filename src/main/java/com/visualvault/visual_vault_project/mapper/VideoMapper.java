package com.visualvault.visual_vault_project.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.visualvault.visual_vault_project.dto.VideoCreateDTO;
import com.visualvault.visual_vault_project.dto.VideoResponseDTO;
import com.visualvault.visual_vault_project.entity.Categoria;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.entity.VideoEtiqueta;
import com.visualvault.visual_vault_project.repository.CategoriaRepository;
import com.visualvault.visual_vault_project.services.ThumbnailService;

public class VideoMapper {
    
    public static Video toEntity(VideoCreateDTO dto, Usuario usuario, CategoriaRepository categoriaRepository, ThumbnailService thumbnailService) {
        Video video = new Video();
        video.setTitulo(dto.titulo());
        video.setDescripcion(dto.descripcion());
        video.setUrl(dto.url());
        video.setFuente(dto.fuente());
        video.setMiniaturaUrl(dto.miniaturaUrl());
        video.setVisto(dto.visto() != null ? dto.visto() : false);
        video.setUsuario(usuario);

        String miniaturaUrl = thumbnailService.extractThumbnailUrl(dto.url(), dto.fuente());
        video.setMiniaturaUrl(miniaturaUrl != null ? miniaturaUrl : dto.miniaturaUrl());
        
        video.setVisto(dto.visto() != null ? dto.visto() : false);

         Categoria categoria = categoriaRepository.findByNombre(dto.categoria())
            .orElseThrow(() -> new IllegalArgumentException(
                "Categoría no encontrada: " + dto.categoria()));
    video.setCategoria(categoria);

        // Convertir List<String> a Set<VideoEtiqueta> rebisar logica
        if (dto.etiquetas() != null) {
        Set<VideoEtiqueta> etiquetas = dto.etiquetas().stream()
                .map(nombre -> {
                    VideoEtiqueta etiqueta = new VideoEtiqueta();
                    etiqueta.setNombre(nombre);
                    etiqueta.setVideo(video); 
                    return etiqueta;
                })
                .collect(Collectors.toSet());
        video.setEtiquetas(etiquetas);
    }

        return video;
    }

    public static VideoResponseDTO toDTO(Video video) {
        List<String> etiquetas = video.getEtiquetas() != null
                ? video.getEtiquetas().stream()
                    .map(VideoEtiqueta::getNombre)
                    .toList()
                : List.of();

        return new VideoResponseDTO(
                video.getIdVideo(),
                video.getTitulo(),
                video.getDescripcion(),
                video.getUrl(),
                video.getMiniaturaUrl(),
                video.getFuente(),
                video.getVisto(),
                video.getCategoria() != null ? video.getCategoria().getNombre() : null,
                etiquetas,
                video.getFechaGuardado(),
                video.getUsuario() != null ? video.getUsuario().getUsername() : null
        );
    }
}