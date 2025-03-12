package com.okhara.rating_system.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash("reset_password_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordCode {

    @Id
    @Indexed
    private Long id;

    @Indexed
    private Long userId;

    @Indexed
    private String code;

    private Instant expiryDate;
}
