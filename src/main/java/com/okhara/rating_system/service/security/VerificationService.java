package com.okhara.rating_system.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class VerificationService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "verification_codes:";

    @Value("${app.domain}")
    private String domainAddress;

    String generateLink(String email) {
        String key = PREFIX + email;
        String verificationCode = String.valueOf(UUID.randomUUID());
        redisTemplate.opsForValue().set(key, verificationCode);
        return domainAddress + "/auth/verify?code=" + verificationCode;
    }

    void deleteCode(String email) {
        redisTemplate.delete(PREFIX + email);
    }

    boolean validateCode(String email, String code) {
        String storedCode = getCode(email);
        return storedCode != null && storedCode.equals(code);
    }

    String getCode(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }
}
