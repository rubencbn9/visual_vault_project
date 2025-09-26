package com.visualvault.visual_vault_project.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "etiquetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEtiqueta;

    private String nombre;

    @OneToMany(mappedBy = "etiquetas", cascade = CascadeType.ALL)
    private Set<VideoEtiqueta> videos;
}
