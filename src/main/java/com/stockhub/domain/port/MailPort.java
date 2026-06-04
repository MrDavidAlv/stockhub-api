package com.stockhub.domain.port;

public interface MailPort {

    void enviarPdfAdjunto(
            String destinatario,
            String subject,
            String body,
            byte[] pdf,
            String filename);
}
