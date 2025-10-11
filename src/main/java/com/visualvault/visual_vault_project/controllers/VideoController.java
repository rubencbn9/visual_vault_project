package com.visualvault.visual_vault_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // @Operation(summary = "Obtener lista de videos", description = "Devuelve todos los videos almacenados en la base de datos")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Lista de videos encontrada",
    //         content = @Content(mediaType = "application/json",
    //         schema = @Schema(implementation = Video.class))),
    //     @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    // })
    @GetMapping("/list")
    public List<Video> listaVideos() {
        return videoRepository.findAll();
    }

    // @Operation(summary = "Obtener video por ID", description = "Devuelve los datos de un video específico por su ID")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Video encontrado",
    //         content = @Content(mediaType = "application/json",
    //         schema = @Schema(implementation = Video.class))),
    //     @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content)
    // })
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado"));
    }

    // @Operation(summary = "Crear nuevo video", description = "Crea un nuevo video asignado a un usuario existente")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "201", description = "Video creado exitosamente",
    //         content = @Content(mediaType = "application/json",
    //         schema = @Schema(implementation = Video.class))),
    //     @ApiResponse(responseCode = "400", description = "Error en los datos enviados", content = @Content)
    // })
    
    @PostMapping("/send")
    public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoRequest videoRequest) {
        Usuario usuario = usuarioRepository.findById(videoRequest.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no válido"));

        Video video = Video.builder()
                .titulo(videoRequest.titulo())
                .descripcion(videoRequest.descripcion())
                .usuario(usuario)
                .build();

        Video saved = videoRepository.save(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // @Operation(summary = "Eliminar video", description = "Elimina un video existente por su ID")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "204", description = "Video eliminado exitosamente"),
    //     @ApiResponse(responseCode = "404", description = "Video no encontrado", content = @Content)
    // })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado");
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO interno para recibir datos de creación
    public record VideoRequest(String titulo, String descripcion, Long usuarioId) {}
}
