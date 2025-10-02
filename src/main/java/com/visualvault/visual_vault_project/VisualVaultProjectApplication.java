package com.visualvault.visual_vault_project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;

@SpringBootApplication
public class VisualVaultProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualVaultProjectApplication.class, args);
	}

	 @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, VideoRepository videoRepository) {
        return args -> {
            // Crear usuario
            Usuario usuario = Usuario.builder()
                    .username("juan123")
                    .email("juan@example.com")
                    .contrasenaHash("1234")
                    .build();
            usuarioRepository.save(usuario);

            // Crear video asignándole el usuario
            Video video = Video.builder()
                    .titulo("Mi primer video")
                    .descripcion("Un video de prueba")
                    .usuario(usuario) 
                    .build();
            videoRepository.save(video);

            System.out.println("Datos iniciales cargados ok");
        };
    }


}
