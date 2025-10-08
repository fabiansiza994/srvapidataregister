package com.fmsp.srvapidataregister.modules.notification.service;

import com.fmsp.srvapidataregister.modules.notification.dto.EmailAttachmentRequest;
import com.fmsp.srvapidataregister.modules.notification.dto.EmailRequest;
import com.fmsp.srvapidataregister.modules.notification.dto.EmailTemplateRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Base64;
import java.util.Objects;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // opcional: inyectado por spring-boot-starter-thymeleaf

    @Value("${spring.mail.username}")
    private String defaultFrom;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /** Texto plano */
    public void sendPlain(EmailRequest req) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(req.to());
        msg.setSubject(req.subject());
        msg.setText(req.body());
        msg.setFrom(defaultFrom);
        mailSender.send(msg);
    }

    /** HTML */
    public void sendHtml(EmailRequest req) throws MessagingException, MailException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
        helper.setFrom(defaultFrom);
        helper.setTo(req.to());
        helper.setSubject(req.subject());
        helper.setText(req.body(), true); // HTML
        mailSender.send(mime);
    }

    /** HTML + adjunto (Base64) */
    public void sendHtmlWithAttachment(EmailAttachmentRequest req) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setFrom(defaultFrom);
        helper.setTo(req.to());
        helper.setSubject(req.subject());
        helper.setText(req.htmlBody(), true);

        byte[] bytes = Base64.getDecoder().decode(req.base64Content());
        ByteArrayResource resource = new ByteArrayResource(bytes);
        helper.addAttachment(req.filename(), resource, req.contentType());

        mailSender.send(mime);
    }

    /** Con plantilla Thymeleaf */
    public void sendTemplate(EmailTemplateRequest req) throws MessagingException {
        Objects.requireNonNull(templateEngine, "TemplateEngine no disponible (agrega starter-thymeleaf)");
        Context ctx = new Context();
        if (req.variables() != null) {
            req.variables().forEach(ctx::setVariable);
        }
        String html = templateEngine.process("mail/" + req.template(), ctx);

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
        helper.setFrom(defaultFrom);
        helper.setTo(req.to());
        helper.setSubject(req.subject());
        helper.setText(html, true);

        mailSender.send(mime);
    }
}
