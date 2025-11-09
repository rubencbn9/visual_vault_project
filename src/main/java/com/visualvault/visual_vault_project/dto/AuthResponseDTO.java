package com.visualvault.visual_vault_project.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String username;
   
    
    public AuthResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
