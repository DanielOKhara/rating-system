package com.okhara.rating_system.service.email;

import com.okhara.rating_system.model.auth.AppUser;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendConfirmationEmail(AppUser user){
        System.out.println("Send email to " + user.getNickname() + " to address " + user.getEmail());
    }
}
