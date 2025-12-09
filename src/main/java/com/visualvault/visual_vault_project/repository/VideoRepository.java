package com.visualvault.visual_vault_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;

public interface VideoRepository extends JpaRepository<Video ,Long>{
    // Buscar todos los videos de un usuario 
    List<Video> findByUsuario(Usuario usuario);
    
    // Alternativa con query explícita
    @Query("SELECT v FROM Video v WHERE v.usuario.idUsuario = :idUsuario")
    List<Video> findByUsuarioId(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT v FROM Video v WHERE v.usuario = :usuario AND v.categoria.nombre = :categoriaNombre")
    List<Video> findByUsuarioAndCategoriaNombre(@Param("usuario") Usuario usuario, @Param("categoriaNombre") String categoriaNombre);
    
    // Buscar videos por fuente de usuario
    List<Video> findByUsuarioAndFuente(Usuario usuario, String fuente);
    
    // Buscar videos vistos/no vistos de un usuario
    List<Video> findByUsuarioAndVisto(Usuario usuario, Boolean visto);
    
    // Buscar videos por título que contenga un texto 
    List<Video> findByUsuarioAndTituloContainingIgnoreCase(Usuario usuario, String titulo);

    List<Video> findByVistoTrue();
    List<Video> findByVistoFalse();
    
}
