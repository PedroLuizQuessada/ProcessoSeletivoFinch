package com.pedroquessada.processoseletivofinch.util;

import com.pedroquessada.processoseletivofinch.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;

@Service
public class EmailUtil {

    @Value("${spring.mail.nome}")
    private String remetenteNome;

    @Value("${spring.mail.username}")
    private String remetenteEmail;

    private final JavaMailSender mailSender;

    public EmailUtil(JavaMailSender a) {
        this.mailSender = a;
    }

    public void enviarEmail(String assunto, String mensagem, String destinatario, File file) throws EmailException {
        if (destinatario.equalsIgnoreCase("n")) {
            return;
        }

        try {
            MimeMessage email = mailSender.createMimeMessage();
            Multipart multipart = new MimeMultipart();

            MimeMessageHelper helper = new MimeMessageHelper(email);
            helper.setFrom(remetenteEmail, remetenteNome);
            helper.setTo(destinatario);
            helper.setSubject(assunto);

            BodyPart texto = new MimeBodyPart();
            texto.setText(mensagem);
            multipart.addBodyPart(texto);

            if (file != null) {
                MimeBodyPart anexos = new MimeBodyPart();
                anexos.attachFile(file);
                multipart.addBodyPart(anexos);
            }

            email.setContent(multipart);
            mailSender.send(email);
        }
        catch (MessagingException | IOException e) {
            throw new EmailException();
        }
    }
}