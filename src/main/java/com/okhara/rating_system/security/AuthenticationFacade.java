package com.okhara.rating_system.security;

import com.okhara.rating_system.model.auth.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("Unexpected authentication principal type");
    }
}
