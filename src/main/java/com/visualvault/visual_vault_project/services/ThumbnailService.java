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
 * Extrae el ID de un video de Dailymotion y devuelve su miniatura
 */
public String extractDailymotionThumbnail(String videoUrl) {
    if (videoUrl == null || videoUrl.isEmpty()) {
        return null;
    }

    String videoId = null;

    // Formato: https://www.dailymotion.com/video/x7y8z9
    if (videoUrl.contains("dailymotion.com/video/")) {
        videoId = videoUrl.split("dailymotion.com/video/")[1].split("[/?#]")[0];
    }
    // Formato corto: https://dai.ly/x7y8z9
    else if (videoUrl.contains("dai.ly/")) {
        videoId = videoUrl.split("dai.ly/")[1].split("[/?#]")[0];
    }

    if (videoId == null || videoId.isEmpty()) {
        return null;
    }

    // URL directa de la miniatura
    return "https://www.dailymotion.com/thumbnail/video/" + videoId;
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

            case "twitch":
                return extractTwitchThumbnailUrl(videoUrl);

            case "vimeo":
                return extractVimeoThumbnail(videoUrl);

            case "tiktok":
                return extractTikTokThumbnail(videoUrl);

            default:
                return null;
        }
    }

    /**
     * Extrae la miniatura de un clip de Twitch
     */
    public String extractTwitchThumbnailUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        String clipId = null;

        // Ejemplo: https://clips.twitch.tv/NombreDelClip
        if (videoUrl.contains("clips.twitch.tv/")) {
            clipId = videoUrl.split("clips.twitch.tv/")[1].split("[/?#]")[0];
        }
        // Ejemplo: https://www.twitch.tv/usuario/clip/NombreDelClip
        else if (videoUrl.contains("/clip/")) {
            clipId = videoUrl.split("/clip/")[1].split("[/?#]")[0];
        }

        if (clipId == null || clipId.isEmpty()) {
            return null;
        }

        // Devuelve la URL de la miniatura del clip
        return "https://clips-media-assets2.twitch.tv/" + clipId + "-preview-480x272.jpg";
    }

    /**
     * Para Vimeo (requiere API, aquí devolvemos null)
     */
    private String extractVimeoThumbnail(String url) {
        // Implementación básica - aquí irá la API de Vimeo
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
