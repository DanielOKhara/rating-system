package com.okhara.rating_system.service.security;

import com.okhara.rating_system.exception.VerificationException;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.VerificationCode;
import com.okhara.rating_system.repository.redis.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    @Value("${app.verification.verificationCodeExpiration}")
    private Duration verificationCodeExpiration;

    private final VerificationCodeRepository codeRepository;

    @Value("${app.domain}")
    private String domainAddress;

    public String generateLink(AppUser user) {
        String userCode = UUID.randomUUID().toString();
        var verificationCode = VerificationCode.builder()
                .userId(user.getId())
                .expiryDate(Instant.now().plusMillis(verificationCodeExpiration.toMillis()))
                .code(userCode)
                .build();

        codeRepository.save(verificationCode);
        return domainAddress + "/auth/verify?code=" + userCode;
    }

    void deleteCode(Long userId) {
        codeRepository.deleteByUserId(userId);
    }

    public Long verifyUserAndGetIdIfSuccess(String code) {
        Optional<VerificationCode> verificationCode = codeRepository.findByCode(code);
        if(verificationCode.isEmpty()){
            throw new VerificationException("No responses to activate account or it's already activated");
        }
        codeRepository.delete(verificationCode.get());
        return verificationCode.get().getUserId();
    }

}