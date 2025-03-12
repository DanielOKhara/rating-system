package com.okhara.rating_system.service.email;

import com.okhara.rating_system.exception.EmailError;
import com.okhara.rating_system.model.auth.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String ERROR_MESSAGE = "E-mail sending was rejected, please repeat later";

    public void sendVerificationEmail(String to, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verification mail");
            message.setText("Verify link: \n" + link);
            message.setFrom("final.project.petrachkou@gmail.com");

            mailSender.send(message);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new EmailError(ERROR_MESSAGE);
        }
    }

    public void sendConfirmationEmail(AppUser user){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Your account activated");
            message.setText(MessageFormat.format("Dear {0}, you account activated!", user.getNickname()));
            message.setFrom("final.project.petrachkou@gmail.com");

            mailSender.send(message);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new EmailError(ERROR_MESSAGE);
        }
    }

    public void sendRejectionEmail(AppUser user){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Your account activation rejected");
            message.setText(MessageFormat.format("Dear {0}, you account activation rejected! \n" +
                    "Reason for refusal: violation of community rules", user.getNickname()));
            message.setFrom("final.project.petrachkou@gmail.com");

            mailSender.send(message);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new EmailError(ERROR_MESSAGE);
        }
    }

    public void sendResetPasswordEmail(String to, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset password mail");
            message.setText("Reset password link: \n" + link);
            message.setFrom("final.project.petrachkou@gmail.com");

            mailSender.send(message);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new EmailError(ERROR_MESSAGE);
        }
    }
}
