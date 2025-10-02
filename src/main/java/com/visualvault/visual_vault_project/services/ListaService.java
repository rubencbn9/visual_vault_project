package com.visualvault.visual_vault_project.services;

import java.util.List;

import com.visualvault.visual_vault_project.entity.Lista;

public interface ListaService {

    Lista crearLista(Lista lista);

    List<Lista> obtenerListasPorUsuario(Long usuarioId);

    Lista agregarVideo(Long listaId, Long videoId);

    Lista eliminarVideo(Long listaId, Long videoId);

}
