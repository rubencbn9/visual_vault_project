package com.visualvault.visual_vault_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visualvault.visual_vault_project.dto.ContactoDTO;
import com.visualvault.visual_vault_project.services.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contacto")
@Tag(name = "Contacto", description = "API para el formulario de contacto")
public class ContactoController {

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Enviar mensaje de contacto", description = "Envía un mensaje desde el formulario de contacto")
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarContacto(@Valid @RequestBody ContactoDTO contacto) {
        try {
            emailService.enviarMensajeContacto(contacto);
            return ResponseEntity.ok()
                    .body(new MensajeRespuesta("Mensaje enviado correctamente. Te responderemos pronto."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeRespuesta("Error al enviar el mensaje. Inténtalo de nuevo."));
        }
    }

    // Clase interna para la respuesta
    private record MensajeRespuesta(String mensaje) {
    }
}