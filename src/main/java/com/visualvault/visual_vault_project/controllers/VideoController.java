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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visualvault.visual_vault_project.dto.VideoCreateDTO;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.mapper.VideoMapper;
import com.visualvault.visual_vault_project.repository.CategoriaRepository;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import com.visualvault.visual_vault_project.services.ThumbnailService;

    @RestController
    @RequestMapping("/api/videos")
    @CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
    public class VideoController {

        @Autowired
        private VideoRepository videoRepository;
        
        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private ThumbnailService thumbnailService;



        // Obtener usuario actual desde el contexto de seguridad
        private Usuario getCurrentUser(Authentication auth) {
            if (auth == null || auth.getName() == null) {
                throw new RuntimeException("No hay autenticación válida");
            }
            
            String username = auth.getName();
            System.out.println("Usuario autenticado: " + username); // LOG
            
            Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));;
            
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado: " + username);
            }
            
            System.out.println("Usuario encontrado - ID: " + usuario.getIdUsuario()); // LOG
            return usuario;
        }



        // Obtener todos los videos del usuario autenticado
        @GetMapping("/list")
        public ResponseEntity<List<Video>> getVideos(Authentication auth) {
            try {
                Usuario usuario = getCurrentUser(auth);
                System.out.println("Buscando videos del usuario: " + usuario.getUsername()); // LOG
                
                List<Video> videos = videoRepository.findByUsuario(usuario);
                
                System.out.println("Videos encontrados: " + videos.size()); // LOG
                
                return ResponseEntity.ok(videos);
            } catch (Exception e) {
                System.err.println("Error al obtener videos: " + e.getMessage()); // LOG
                e.printStackTrace();
                return ResponseEntity.status(401).build();
            }
        }

        // Obtener un video específico del usuario
        @GetMapping("/{id}")
        public ResponseEntity<Video> getVideo(@PathVariable Long id, Authentication auth) {
            try {
                Usuario usuario = getCurrentUser(auth);
                Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));
                
                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    System.out.println("Acceso denegado: Video no pertenece al usuario"); // LOG
                    return ResponseEntity.status(403).build();
                }
                
                return ResponseEntity.ok(video);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return ResponseEntity.status(401).build();
            }
        }

     @PostMapping("/send")
public ResponseEntity<?> createVideo(@RequestBody VideoCreateDTO videoDTO, Authentication auth) {
    try {
        Usuario usuario = getCurrentUser(auth);
        System.out.println("Creando video para usuario: " + usuario.getUsername());

        // Asegurar que la fecha se establezca si no viene
        LocalDateTime fechaGuardado = (videoDTO.fechaGuardado() != null)
                ? videoDTO.fechaGuardado()
                : LocalDateTime.now();

        //  Crear el Video usando el mapper
        Video video = VideoMapper.toEntity(videoDTO, usuario, categoriaRepository, thumbnailService);
        video.setFechaGuardado(fechaGuardado);

        // Guardar el video
        Video savedVideo = videoRepository.save(video);

        System.out.println(" Video creado con ID: " + savedVideo.getIdVideo());
        return ResponseEntity.ok(VideoMapper.toDTO(savedVideo));

    } catch (IllegalArgumentException e) {
        // Si la categoría no existe
        System.err.println(" Error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    } catch (Exception e) {
        System.err.println(" Error inesperado al crear video: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
    }
}

        // Actualizar un video existente
        @PutMapping("/{id}")
        public ResponseEntity<Video> updateVideo(@PathVariable Long id, 
                                                @RequestBody Video videoDetails, 
                                                Authentication auth) {
            try {
                Usuario usuario = getCurrentUser(auth);
                Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));
                
                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    return ResponseEntity.status(403).build();
                }
                
                // Actualizar campos
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

        // Eliminar un video
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteVideo(@PathVariable Long id, Authentication auth) {
            try {
                Usuario usuario = getCurrentUser(auth);
                Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));
                
                System.out.println("Video pertenece a usuario ID: " + video.getUsuario().getIdUsuario()); // LOG
                System.out.println("Usuario actual ID: " + usuario.getIdUsuario()); // LOG
                
                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    System.out.println("Acceso denegado: Video no pertenece al usuario"); // LOG
                    return ResponseEntity.status(403).body("No tienes permiso para eliminar este video");
                }
                
                videoRepository.delete(video);
                System.out.println("Video eliminado correctamente"); // LOG
                return ResponseEntity.ok().body("Video eliminado correctamente");
            } catch (RuntimeException e) {
                System.err.println("Error: " + e.getMessage()); // LOG
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                System.err.println("Error de autenticación: " + e.getMessage()); // LOG
                return ResponseEntity.status(401).build();
            }
        }

        // Marcar video como visto/no visto
        @PatchMapping("/{id}/visto")
        public ResponseEntity<Video> toggleVisto(@PathVariable Long id, Authentication auth) {
            try {
                Usuario usuario = getCurrentUser(auth);
                Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Video no encontrado"));
                
                // Verificar que el video pertenece al usuario
                if (!video.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    return ResponseEntity.status(403).build();
                }
                
                video.setVisto(!video.getVisto());
                Video updatedVideo = videoRepository.save(video);
                return ResponseEntity.ok(updatedVideo);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return ResponseEntity.status(401).build();
            }
        }
    }