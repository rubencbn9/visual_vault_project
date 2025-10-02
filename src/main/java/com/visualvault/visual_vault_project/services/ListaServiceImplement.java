package com.visualvault.visual_vault_project.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.visualvault.visual_vault_project.entity.Lista;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.entity.VideoLista;
import com.visualvault.visual_vault_project.repository.ListaRepository;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoListaRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;

@Service
public class ListaServiceImplement  implements ListaService{
    
    @Autowired
    ListaRepository listaRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    VideoListaRepository videoListaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

   public Lista crearLista(Lista lista){
    return listaRepository.save(lista);
   }

public List<Lista> obtenerListasPorUsuario(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
    
    return usuario.getListas(); // Devuelve directamente las listas asociadas al usuario
}


   public Lista agregarVideo(Long listaId, Long videoId) {
        Optional<Lista> listaOpt = listaRepository.findById(listaId);
        Optional<Video> videoOpt = videoRepository.findById(videoId);

        if (listaOpt.isPresent() && videoOpt.isPresent()) {
            Lista lista = listaOpt.get();
            Video video = videoOpt.get();

            // Crear entidad puente
            VideoLista videoLista = new VideoLista();
            videoLista.setLista(lista);
            videoLista.setVideo(video);

            videoListaRepository.save(videoLista);

            return listaRepository.findById(listaId).orElseThrow();
        } else {
            throw new RuntimeException("Lista o Video no encontrado");
        }
    }

   public Lista eliminarVideo(Long listaId, Long videoId) {
        Optional<Lista> listaOpt = listaRepository.findById(listaId);
        Optional<Video> videoOpt = videoRepository.findById(videoId);

        if (listaOpt.isPresent() && videoOpt.isPresent()) {
            Lista lista = listaOpt.get();
            Video video = videoOpt.get();

            // Buscar relación VideoLista
            VideoLista videoLista = videoListaRepository.findByListaAndVideo(lista, video)
                    .orElseThrow(() -> new RuntimeException("Relación Lista-Video no encontrada"));

            videoListaRepository.delete(videoLista);

            return lista;
        } else {
            throw new RuntimeException("Lista o Video no encontrado");
        }
    }

}
    
