package com.fmsp.srvapidataregister.modules.notification.controller;

import com.fmsp.srvapidataregister.modules.notification.dto.EmailAttachmentRequest;
import com.fmsp.srvapidataregister.modules.notification.dto.EmailRequest;
import com.fmsp.srvapidataregister.modules.notification.dto.EmailTemplateRequest;
import com.fmsp.srvapidataregister.modules.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/send/plain")
    public ResponseEntity<?> sendPlain(@RequestBody @Valid EmailRequest req) {
        emailService.sendPlain(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/html")
    public ResponseEntity<?> sendHtml(@RequestBody @Valid EmailRequest req) throws MessagingException {
        emailService.sendHtml(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/attachment")
    public ResponseEntity<?> sendWithAttachment(@RequestBody @Valid EmailAttachmentRequest req) throws MessagingException {
        emailService.sendHtmlWithAttachment(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/template")
    public ResponseEntity<?> sendTemplate(@RequestBody @Valid EmailTemplateRequest req) throws MessagingException {
        emailService.sendTemplate(req);
        return ResponseEntity.ok().build();
    }

}
