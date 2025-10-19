package com.visualvault.visual_vault_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.visualvault.visual_vault_project.dto.VideoCreateDTO;
import com.visualvault.visual_vault_project.dto.VideoResponseDTO;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.mapper.VideoMapper;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:5500") // ⚙️ Ajusta el puerto si tu front usa otro
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 🔹 GET: Lista todos los videos
    @GetMapping("/list")
    public List<VideoResponseDTO> listaVideos() {
        return videoRepository.findAll()
                .stream()
                .map(VideoMapper::toDTO)
                .toList();
    }

    // 🔹 GET: Obtener video por ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> getVideoById(@PathVariable Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado"));

        return ResponseEntity.ok(VideoMapper.toDTO(video));
    }

    // 🔹 POST: Crear nuevo video
    @PostMapping("/send")
    public ResponseEntity<VideoResponseDTO> createVideo(@Valid @RequestBody VideoCreateDTO videoDTO) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(videoDTO.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no válido"));

        // Convertir DTO → Entidad
        Video video = VideoMapper.toEntity(videoDTO, usuario);

        // Guardar en base de datos
        Video saved = videoRepository.save(video);

        // Devolver entidad convertida a DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(VideoMapper.toDTO(saved));
    }

    // 🔹 DELETE: Eliminar video por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video no encontrado");
        }

        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}