package com.visualvault.visual_vault_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video_etiqueta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoEtiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVideoEtiqueta;

    @ManyToOne
    @JoinColumn(name = "id_video", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "id_etiqueta", nullable = false)
    private Etiqueta etiqueta;
}
