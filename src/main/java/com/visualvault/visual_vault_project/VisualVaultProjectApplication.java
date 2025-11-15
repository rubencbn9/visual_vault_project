package com.visualvault.visual_vault_project;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.visualvault.visual_vault_project.entity.Categoria;
import com.visualvault.visual_vault_project.entity.Usuario;
import com.visualvault.visual_vault_project.entity.Video;
import com.visualvault.visual_vault_project.repository.UsuarioRepository;
import com.visualvault.visual_vault_project.repository.VideoRepository;
import com.visualvault.visual_vault_project.services.CategoriaService;

@SpringBootApplication
public class VisualVaultProjectApplication {

     @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(VisualVaultProjectApplication.class, args);
	}

	 @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, VideoRepository videoRepository, CategoriaService categoriaService) {
        return args -> {
            // Crear usuario
            Usuario usuario1 = Usuario.builder()
                    .username("Ruben")
                    .email("ruben@example.com")
                    .contrasenaHash(passwordEncoder.encode("12345678"))
                    .build();
            usuarioRepository.save(usuario1);

                Usuario usuario2 = Usuario.builder()
                    .username("Lucia")
                    .email("lucia@example.com")
                    .contrasenaHash(passwordEncoder.encode("12345678"))
                    .build();
            usuarioRepository.save(usuario2);

            // Crear categoria 
            Categoria gaming = categoriaService.crearcategoria(new Categoria(null,"Gaming", null));    
        Categoria entretenimiento = categoriaService.crearcategoria(new Categoria(null,"Entretenimiento", null));    
        Categoria educacion = categoriaService.crearcategoria(new Categoria(null, "Educacion", null));
        Categoria Deportes = categoriaService.crearcategoria(new Categoria(null, "Deportes", null));
        Categoria Musica = categoriaService.crearcategoria(new Categoria(null, "Musica", null));
                        

            //  Crear video asignándole el usuario
            Video video = Video.builder()
                    .titulo("Mi primer video")
                    .descripcion("Un video de prueba")
                    .fuente("YouTube")
                    .categoria(educacion)
                    .usuario(usuario1)
                    .miniaturaUrl("https://img.youtube.com/vi/Jp7sf6CUaxU/hqdefault.jpg")
                    .url("https://www.youtube.com/watch?v=Jp7sf6CUaxU&pp=0gcJCfwJAYcqIYzv")
                    .fechaGuardado(LocalDateTime.of(2025, 10, 19, 15, 30, 0)) 
                    .build();
            videoRepository.save(video);

             Video video2 = Video.builder()
                    .titulo("Mi segundo video")
                    .descripcion("Un video de prueba en usuario 2")
                    .fuente("YouTube")
                    .categoria(educacion)
                    .usuario(usuario2)
                    .miniaturaUrl("https://img.youtube.com/vi/OcM16gCD8o4/hqdefault.jpg")
                    .url("https://www.youtube.com/watch?v=OcM16gCD8o4")
                    .fechaGuardado(LocalDateTime.of(2025, 10, 19, 15, 30, 0)) 
                    .build();
            videoRepository.save(video2);

            System.out.println("Datos iniciales cargados ok");
        };
    }


}
