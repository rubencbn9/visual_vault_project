package com.visualvault.visual_vault_project.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.visualvault.visual_vault_project.dto.UsuarioCreateDTO;
import com.visualvault.visual_vault_project.dto.UsuarioResponseDTO;
import com.visualvault.visual_vault_project.entity.Usuario;

public class UsuarioMapper {

    //podría hacerse con estructura lombock

    public static Usuario toEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        usuario.setEmail(dto.email());
        usuario.setContrasenaHash(dto.contrasenaHash());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setVideos(List.of()); // inicializa vacío
        usuario.setListas(List.of()); // inicializa vacío
        return usuario;
    }

    public static UsuarioResponseDTO toDTO(Usuario usuario){
        return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getUsername(),
            usuario.getEmail()
        );
    }
}
