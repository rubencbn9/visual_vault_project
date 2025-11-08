package com.visualvault.visual_vault_project.mapper;

import com.visualvault.visual_vault_project.dto.ListaCreateDTO;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Lista;

public class ListaMapper {
    

    public static Lista toEntity(ListaCreateDTO dto, Usuario usuario){
        Lista lista = new Lista();
        lista.setIdLista(dto.idLista());
        lista.setNombre(dto.nombre());
        lista.setUsuario(usuario);
        lista.setVideo(null);
        return lista;
    }


    public static ListaCreateDTO toDTO(Lista lista){
        return new ListaCreateDTO(
            lista.getIdLista(),
            lista.getNombre()
        );
    }
}
