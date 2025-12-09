package com.visualvault.visual_vault_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactoDTO(
        @NotBlank(message = "El nombre es obligatorio") 
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") 
        String nombre,

        @NotBlank(message = "El email es obligatorio") 
        @Email(message = "El email debe ser válido") 
        String email,

        @NotBlank(message = "El asunto es obligatorio")
        String asunto,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(min = 10, max = 1000, message = "El mensaje debe tener entre 10 y 1000 caracteres")
        String mensaje) {

}
