package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.exception.EmailSendException;
import com.uade.cookitbackend.exception.EmailTemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;  // solo si quieres imágenes embebidas
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendHtmlMessage(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            Context ctx = new Context();
            ctx.setVariables(variables);
            String html;
            
            try {
                html = templateEngine.process(templateName, ctx);
            } catch (TemplateProcessingException e) {
                throw new EmailTemplateException("Error procesando template de email: " + templateName, e);
            }

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            try {
                helper.addInline(
                        "logo",
                        new ClassPathResource("templates/assets/cookit-logo.png")
                );
            } catch (MessagingException e) {
                throw new EmailTemplateException("Error agregando recursos al email", e);
            }

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new EmailSendException("Error enviando correo a: " + to, e);
            }
            
        } catch (MessagingException e) {
            throw new EmailSendException("Error configurando mensaje de correo", e);
        } catch (EmailSendException | EmailTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendException("Error inesperado enviando correo HTML", e);
        }
    }
}
