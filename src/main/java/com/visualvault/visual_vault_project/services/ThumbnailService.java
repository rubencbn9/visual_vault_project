package com.visualvault.visual_vault_project.services;

import org.springframework.stereotype.Service;

@Service
public class ThumbnailService {

    /**
     * Extrae el ID de un video de YouTube desde la URL
     */
    public String extractYouTubeVideoId(String url) {
        String videoId = null;
        
        // Formato: https://www.youtube.com/watch?v=VIDEO_ID
        if (url.contains("youtube.com/watch")) {
            videoId = url.split("v=")[1].split("&")[0];
        }
        // Formato: https://youtu.be/VIDEO_ID
        else if (url.contains("youtu.be/")) {
            videoId = url.split("youtu.be/")[1].split("\\?")[0];
        }
        
        return videoId;
    }

    /**
     * Genera la URL de la miniatura de YouTube
     * Otras opciones: default, mqdefault, sddefault, maxresdefault
     */
    public String getYouTubeThumbnailUrl(String youtubeUrl) {
        String videoId = extractYouTubeVideoId(youtubeUrl);
        
        if (videoId == null || videoId.isEmpty()) {
            return null;
        }
        
        // Retorna la URL de la miniatura de máxima calidad disponible
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    /**
     * Extrae miniatura según la plataforma
     */
    public String extractThumbnailUrl(String videoUrl, String plataforma) {
        if (plataforma == null) {
            return null;
        }
        
        switch (plataforma.toLowerCase()) {
            case "youtube":
                return getYouTubeThumbnailUrl(videoUrl);
            
            case "vimeo":
                return extractVimeoThumbnail(videoUrl);
            
            case "tiktok":
                return extractTikTokThumbnail(videoUrl);
            
            default:
                return null;
        }
    }

    /**
     * Para Vimeo (requiere API, aquí devolvemos null)
     */
    private String extractVimeoThumbnail(String url) {
        // Implementación básica - aqui irá la API de Vimeo
        return null;
    }

    /**
     * Para TikTok (requiere scraping, aquí devolvemos null)
     */
    private String extractTikTokThumbnail(String url) {
        // Implementación básica - TikTok no facilita acceso directo
        return null;
    }
}
