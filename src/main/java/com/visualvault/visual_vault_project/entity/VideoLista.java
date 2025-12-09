package com.visualvault.visual_vault_project.entity;

import java.time.LocalDateTime;

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
@Table(name = "video_lista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//------------------------------------------------------
//NO ESTA EN USO
//------------------------------------------------------
public class VideoLista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVideoLista;

    @ManyToOne
    @JoinColumn(name = "id_video", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "id_lista", nullable = false)
    private Lista lista;

    // Extra: fecha en la que se añadió el vídeo a la lista
    private LocalDateTime fechaAgregado = LocalDateTime.now();
}

