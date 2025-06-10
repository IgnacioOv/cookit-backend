package com.uade.cookitbackend.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;  // solo si quieres imágenes embebidas
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
            String html = templateEngine.process(templateName, ctx);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            helper.addInline(
                    "logo",
                    new ClassPathResource("templates/assets/cookit-logo.png")
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo HTML", e);
        }
    }
}
