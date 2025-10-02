package com.visualvault.visual_vault_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Lista;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.entity.VideoLista;

public interface VideoListaRepository extends JpaRepository<VideoLista, Long> {
    Optional<VideoLista> findByListaAndVideo(Lista lista, Video video);
}
