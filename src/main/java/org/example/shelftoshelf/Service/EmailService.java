package org.example.shelftoshelf.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("selftoself2026@gmail.com");
            message.setTo(email);
            message.setSubject("ShelfToShelf OTP Təsdiqləmə Kodu");
            message.setText("Salam,\n\nShelfToShelf platformasında qeydiyyatı tamamlamaq üçün OTP kodunuz: " + otp + 
                    "\nBu kod 5 dəqiqə ərzində etibarlıdır.\n\nShelfToShelf Komandası");

            mailSender.send(message);
            log.info("OTP uğurla göndərildi: {}", email);
        } catch (Exception e) {
            log.warn("E-poçt göndərilərkən xəta baş verdi: {}. OTP konsola yazıldı.", e.getMessage());
            System.out.println("=================================================");
            System.out.println(">>> [ShelfToShelf OTP GÖNDƏRİLDİ] <<<");
            System.out.println("Email: " + email);
            System.out.println("OTP Kod: " + otp);
            System.out.println("=================================================");
        }
    }
}