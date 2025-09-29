package com.visualvault.visual_vault_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visualvault.visual_vault_project.entity.Video;

public interface VideoRepository extends JpaRepository<Video ,Long>{
    
}
