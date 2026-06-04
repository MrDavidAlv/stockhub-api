package com.stockhub.infrastructure.mail;

import com.stockhub.domain.port.MailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailAdapter implements MailPort {

    private final JavaMailSender sender;

    @Value("${mail.from:no-reply@stockhub.local}")
    private String from;

    @Override
    public void enviarPdfAdjunto(
            String destinatario, String subject, String body, byte[] pdf, String filename) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.addAttachment(filename, new ByteArrayResource(pdf), "application/pdf");
            sender.send(message);
            log.info("Email enviado a {} con adjunto {} ({} bytes)", destinatario, filename, pdf.length);
        } catch (MessagingException | MailException e) {
            throw new MailDeliveryException("Error enviando email a " + destinatario, e);
        }
    }
}
