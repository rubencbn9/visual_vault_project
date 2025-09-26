package com.visualvault.visual_vault_project.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_video;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String url;
    private String titulo;
    private String descripcion;
    private String miniaturaUrl;
    private String fuente;
    private String estado;
    private LocalDateTime fechaGuardado = LocalDateTime.now();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private Set<VideoCategoria> categorias;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private Set<VideoEtiqueta> etiquetas;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private Set<VideoLista> listas;

}
