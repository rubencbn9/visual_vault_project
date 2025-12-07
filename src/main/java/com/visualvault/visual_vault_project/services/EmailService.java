package com.visualvault.visual_vault_project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.visualvault.visual_vault_project.dto.ContactoDTO;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.contact.email}")
    private String contactEmail;

    public void enviarMensajeContacto(ContactoDTO contacto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(contactEmail);
        mensaje.setFrom(contactEmail); // Gmail requiere que sea tu email
        mensaje.setReplyTo(contacto.email()); // Para que puedas responder directamente
        mensaje.setSubject("Contacto VideoVault: " + contacto.asunto());

        String cuerpo = String.format(
                """
                        Nuevo mensaje de contacto desde VideoVault

                        Nombre: %s
                        Email: %s
                        Asunto: %s

                        Mensaje:
                        %s

                        ---
                        Este mensaje fue enviado desde el formulario de contacto de VideoVault.
                        """,
                contacto.nombre(),
                contacto.email(),
                contacto.asunto(),
                contacto.mensaje());

        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
    }

}
