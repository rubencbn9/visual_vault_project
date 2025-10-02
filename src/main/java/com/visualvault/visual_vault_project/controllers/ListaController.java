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

import com.visualvault.visual_vault_project.entity.Lista;
import com.visualvault.visual_vault_project.services.ListaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    @Autowired
    private ListaService listaService;

    // 🔹 Crear lista
    @PostMapping
    public ResponseEntity<Lista> crearLista(@Valid @RequestBody Lista lista) {
        Lista nuevaLista = listaService.crearLista(lista);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLista);
    }

    // 🔹 Listar listas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Lista>> obtenerListasPorUsuario(@PathVariable Long usuarioId) {
        List<Lista> listas = listaService.obtenerListasPorUsuario(usuarioId);
        return ResponseEntity.ok(listas);
    }

    // 🔹 Agregar video a lista
    @PostMapping("/{listaId}/videos/{videoId}")
    public ResponseEntity<Lista> agregarVideoALista(@PathVariable Long listaId, @PathVariable Long videoId) {
        Lista listaActualizada = listaService.agregarVideo(listaId, videoId);
        return ResponseEntity.ok(listaActualizada);
    }

    // 🔹 Eliminar video de lista
    @DeleteMapping("/{listaId}/videos/{videoId}")
    public ResponseEntity<Lista> eliminarVideoDeLista(@PathVariable Long listaId, @PathVariable Long videoId) {
        Lista listaActualizada = listaService.eliminarVideo(listaId, videoId);
        return ResponseEntity.ok(listaActualizada);
    }
}