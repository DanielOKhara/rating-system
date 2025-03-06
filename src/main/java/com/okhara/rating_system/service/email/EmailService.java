package com.okhara.rating_system.service.email;

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

    public void sendVerificationEmail(String to, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verification mail");
            message.setText("Verify link: \n" + link);
            message.setFrom("final.project.petrachkou@gmail.com");

            mailSender.send(message);
        } catch (Exception ex){

            //todo пересмотри логику обработки этих ошибок! мб выкинуть кастомную и хендлером чисто ловить?
            log.error(ex.getMessage());
        }
    }

    //todo объедени два письма
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
        }
    }
}
