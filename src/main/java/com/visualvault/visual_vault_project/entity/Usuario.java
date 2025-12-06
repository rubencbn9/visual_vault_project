package com.visualvault.visual_vault_project.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idUsuario;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String contrasenaHash;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Rol rol = Rol.USUARIO;

    // Relación con videos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Video> videos;

    // Relación con listas
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Lista> listas;
}
