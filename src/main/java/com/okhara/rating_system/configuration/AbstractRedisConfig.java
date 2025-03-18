package com.okhara.rating_system.configuration;


import com.okhara.rating_system.model.auth.RefreshToken;
import com.okhara.rating_system.model.auth.ResetPasswordCode;
import com.okhara.rating_system.model.auth.VerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.time.Duration;
import java.util.List;

public class AbstractRedisConfig {

    @Value("${app.jwt.refreshTokenExpiration}")
    protected Duration refreshTokenExpiration;

    @Value("${app.verification.verificationCodeExpiration}")
    protected Duration verificationCodeExpiration;

    @Value("${app.password.resetPasswordCodeExpiration}")
    protected Duration resetPasswordCodeExpiration;

    public class AppKeyspaceConfig extends KeyspaceConfiguration {
        private static final String REFRESH_TOKEN_KEYSPACE = "refresh_tokens";
        private static final String VERIFICATION_CODE_KEYSPACE = "verification_codes";
        private static final String RESET_PASSWORD_CODE_KEYSPACE = "reset_password_codes";

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings refreshTokenSettings = new KeyspaceSettings(RefreshToken.class, REFRESH_TOKEN_KEYSPACE);
            refreshTokenSettings.setTimeToLive(refreshTokenExpiration.getSeconds());

            KeyspaceSettings verificationCodeSettings = new KeyspaceSettings(VerificationCode.class, VERIFICATION_CODE_KEYSPACE);
            verificationCodeSettings.setTimeToLive(verificationCodeExpiration.getSeconds());

            KeyspaceSettings resetPasswordCodeSettings = new KeyspaceSettings(ResetPasswordCode.class, RESET_PASSWORD_CODE_KEYSPACE);
            resetPasswordCodeSettings.setTimeToLive(resetPasswordCodeExpiration.getSeconds());

            return List.of(refreshTokenSettings, verificationCodeSettings, resetPasswordCodeSettings);
        }
    }
}
