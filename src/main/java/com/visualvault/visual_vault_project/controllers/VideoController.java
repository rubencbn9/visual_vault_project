package com.visualvault.visual_vault_project.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visualvault.visual_vault_project.dto.VideoCreateDTO;
import com.visualvault.visual_vault_project.config_security.JwtUtil;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.mapper.VideoMapper;
import com.visualvault.visual_vault_project.repository.CategoriaRepository;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import com.visualvault.visual_vault_project.services.ThumbnailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500" })
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private JwtUtil jwtUtil;

    // Obtener usuario actual desde el contexto de seguridad
    private Usuario getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("No hay autenticación válida");
        }

        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario;
    }

    // -------------------------------------------------------------
    // LISTA DE VIDEOS
    // -------------------------------------------------------------
    @Operation(summary = "Obtener todos los videos del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de videos devuelta correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<List<Video>> getVideos(Authentication auth) {
        try {
            Usuario usuario = getCurrentUser(auth);
            List<Video> videos = videoRepository.findByUsuario(usuario);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // -------------------------------------------------------------
    // OBTENER VIDEO POR ID
    // -------------------------------------------------------------
    @Operation(summary = "Obtener un video por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para acceder a este video", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id, Authentication auth) {
        try {
            Usuario usuario = getCurrentUser(auth);
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));

            if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // -------------------------------------------------------------
    // CREAR VIDEO
    // -------------------------------------------------------------
    @Operation(summary = "Crear un nuevo video")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/send")
    public ResponseEntity<?> createVideo(@RequestBody VideoCreateDTO videoDTO, Authentication auth) {
        try {
            Usuario usuario = getCurrentUser(auth);

            LocalDateTime fechaGuardado = (videoDTO.fechaGuardado() != null)
                    ? videoDTO.fechaGuardado()
                    : LocalDateTime.now();

            Video video = VideoMapper.toEntity(videoDTO, usuario, categoriaRepository, thumbnailService);
            video.setFechaGuardado(fechaGuardado);

            Video savedVideo = videoRepository.save(video);
            return ResponseEntity.ok(VideoMapper.toDTO(savedVideo));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    // -------------------------------------------------------------
    // ACTUALIZAR VIDEO
    // -------------------------------------------------------------
    @Operation(summary = "Actualizar un video existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para actualizar este video", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id,
            @RequestBody Video videoDetails,
            Authentication auth) {
        try {
            Usuario usuario = getCurrentUser(auth);
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));

            if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                return ResponseEntity.status(403).build();
            }

            video.setTitulo(videoDetails.getTitulo());
            video.setDescripcion(videoDetails.getDescripcion());
            video.setUrl(videoDetails.getUrl());
            video.setMiniaturaUrl(videoDetails.getMiniaturaUrl());
            video.setFuente(videoDetails.getFuente());
            video.setVisto(videoDetails.getVisto());
            video.setCategoria(videoDetails.getCategoria());

            Video updatedVideo = videoRepository.save(video);
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // -------------------------------------------------------------
    // BORRAR VIDEO
    // -------------------------------------------------------------
    @Operation(summary = "Eliminar un video por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar este video", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, Authentication auth) {
        try {
            Usuario usuario = getCurrentUser(auth);
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));

            if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                return ResponseEntity.status(403).body("No tienes permiso para eliminar este video");
            }

            videoRepository.delete(video);
            return ResponseEntity.ok().body("Video eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // -------------------------------------------------------------
    // TOGGLE VISTO (POR TOKEN)
    // -------------------------------------------------------------
    @Operation(summary = "Alternar el estado visto/no visto de un video")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    // TOGGLE VISTO - VERSIÓN CORREGIDA
    @PatchMapping("/{id}/toggle-visto")
    public ResponseEntity<?> toggleVisto(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication auth) {
        try {
            // 1 Usar Authentication 
            if (auth != null) {
                Usuario usuario = getCurrentUser(auth);

                Video video = videoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Video no encontrado"));

                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("No tienes permiso para modificar este video");
                }

                video.setVisto(!video.getVisto());
                Video updatedVideo = videoRepository.save(video);
                return ResponseEntity.ok(updatedVideo);
            }

            //  2 Usar token directamente 
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                Usuario usuario = usuarioRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Video video = videoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Video no encontrado"));

                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("No tienes permiso para modificar este video");
                }

                video.setVisto(!video.getVisto());
                Video updatedVideo = videoRepository.save(video);
                return ResponseEntity.ok(updatedVideo);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        } catch (RuntimeException e) {
            System.err.println("Error en toggle-visto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno en toggle-visto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // LISTA DE VIDEOS VISTOS
    // -------------------------------------------------------------
    @Operation(summary = "Obtener todos los videos marcados como vistos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de videos devuelta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/vistos")
    public ResponseEntity<List<Video>> getVideosVistos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Video> videosVistos = videoRepository.findByVistoTrue();

            return ResponseEntity.ok(videosVistos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //------------------------------------------------------
    // MARCAR COMO VISTO 
    //------------------------------------------------------
    @PatchMapping("/{id}/marcar-visto")
    public ResponseEntity<?> marcarComoVisto(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication auth) {
        try {
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));

            video.setVisto(true);
            Video updatedVideo = videoRepository.save(video);
            return ResponseEntity.ok(updatedVideo);

        } catch (Exception e) {
            System.err.println("Error en marcar-visto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    //------------------------------------------------------
    // MARCAR COMO NO VISTO 
    //------------------------------------------------------

    @PatchMapping("/{id}/marcar-no-visto")
    public ResponseEntity<?> marcarComoNoVisto(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication auth) {
        try {
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));

            video.setVisto(false);
            Video updatedVideo = videoRepository.save(video);
            return ResponseEntity.ok(updatedVideo);

        } catch (Exception e) {
            System.err.println("Error en marcar-no-visto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // LISTA DE VIDEOS NO VISTOS
    // -------------------------------------------------------------
    @Operation(summary = "Obtener todos los videos no vistos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista devuelta correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/no-vistos")
    public ResponseEntity<List<Video>> getVideosNoVistos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Video> videosNoVistos = videoRepository.findByVistoFalse();

            return ResponseEntity.ok(videosNoVistos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
