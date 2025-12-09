package com.visualvault.visual_vault_project.services;

import org.springframework.stereotype.Service;

@Service
public class ThumbnailService {

    //------------------------------------------------------
    // Extrae el ID de un video de YouTube desde la URL
    //------------------------------------------------------
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

    //------------------------------------------------------
    // Genera la URL de la miniatura de YouTube
    //------------------------------------------------------
    public String getYouTubeThumbnailUrl(String youtubeUrl) {
        String videoId = extractYouTubeVideoId(youtubeUrl);

        if (videoId == null || videoId.isEmpty()) {
            return null;
        }

        // Retorna la URL de la miniatura de máxima calidad disponible
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

     //------------------------------------------------------
     // Extrae el ID de un video de Dailymotion y devuelve su miniatura
     //------------------------------------------------------
    public String extractDailymotionThumbnail(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        String videoId = null;

        // Formato: https://www.dailymotion.com/video/ID
        if (videoUrl.contains("dailymotion.com/video/")) {
            videoId = videoUrl.split("dailymotion.com/video/")[1].split("[/?#]")[0];
        }
        // Formato corto: https://dai.ly/ID
        else if (videoUrl.contains("dai.ly/")) {
            videoId = videoUrl.split("dai.ly/")[1].split("[/?#]")[0];
        }

        if (videoId == null || videoId.isEmpty()) {
            return null;
        }

        // URL directa de la miniatura
        return "https://www.dailymotion.com/thumbnail/video/" + videoId;
    }

     //------------------------------------------------------
     // Extrae miniatura segun la plataforma
     //------------------------------------------------------
    public String extractThumbnailUrl(String videoUrl, String plataforma) {
        if (plataforma == null) {
            return null;
        }

        switch (plataforma.toLowerCase()) {
            case "youtube":
                return getYouTubeThumbnailUrl(videoUrl);

            case "twitch":
                return extractTwitchThumbnailUrl(videoUrl);

            case "vimeo":
                return extractVimeoThumbnail(videoUrl);

            case "tiktok":
                return extractTikTokThumbnail(videoUrl);

            case "dailymotion":
                return extractDailymotionThumbnail(videoUrl);

            default:
                return null;
        }
    }

    //------------------------------------------------------
    // Para Twitch - devuelve imagen generica con logo de Twitch
    //------------------------------------------------------
    public String extractTwitchThumbnailUrl(String videoUrl) {
        // Devuelve la URL de la miniatura del clip
        return "https://i.ibb.co/WpRrsP4X/twitch-logo-1.png";
    }

    //------------------------------------------------------
    // Para Vimeo - devuelve imagen generica con logo de Vimeo
    //------------------------------------------------------
    private String extractVimeoThumbnail(String url) {
        // Imagen genérica con logo de Vimeo (usando un placeholder público)
        return "https://i.ibb.co/k2t4WPcX/vimeo.jpg";
    }

    //------------------------------------------------------
    // Para TikTok - devuelve imagen generica con logo de TikTok
    //------------------------------------------------------
    private String extractTikTokThumbnail(String url) {
        // Imagen genérica con logo de TikTok (usando logo oficial)
        return "https://i.ibb.co/vCSykZMm/tiktok-1.png";
    }
}
