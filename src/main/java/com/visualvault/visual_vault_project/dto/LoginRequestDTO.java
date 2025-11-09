package com.visualvault.visual_vault_project.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private Long idUsuario;
    private String username;
    private String password;
}