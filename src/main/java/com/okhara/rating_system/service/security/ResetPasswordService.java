package com.okhara.rating_system.service.security;

import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.ResetPasswordCode;
import com.okhara.rating_system.repository.redis.ResetPasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResetPasswordService {

    @Value("${app.password.resetPasswordCodeExpiration}")
    private Duration resetPasswordCodeExpiration;

    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    @Value("${app.domain}")
    private String domainAddress;

    String generateLink(AppUser user) {
        String resetPasswordCode = UUID.randomUUID().toString();
        var passwordResetCode = ResetPasswordCode.builder()
                .userId(user.getId())
                .expiryDate(Instant.now().plusMillis(resetPasswordCodeExpiration.toMillis()))
                .code(resetPasswordCode)
                .build();

        resetPasswordCodeRepository.save(passwordResetCode);
        return domainAddress + "/auth/reset?code=" + resetPasswordCode;
    }

    Long getUserIdIfActual(String code) {
        Optional<ResetPasswordCode> resetPasswordCode = resetPasswordCodeRepository.findByCode(code);
        if(resetPasswordCode.isEmpty()){
            throw new CoordinationException("Reset password code is not active!");
        }
        if (resetPasswordCode.get().getExpiryDate().isBefore(Instant.now())) {
            throw new CoordinationException("Reset password code has expired!");
        }
        return resetPasswordCode.get().getUserId();
    }

    public void deleteResetPasswordCode(Long userId){
        resetPasswordCodeRepository.deleteByUserId(userId);
    }
}
