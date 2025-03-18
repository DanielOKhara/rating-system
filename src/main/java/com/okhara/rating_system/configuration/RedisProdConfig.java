package com.okhara.rating_system.configuration;

import com.okhara.rating_system.model.auth.RefreshToken;
import com.okhara.rating_system.model.auth.ResetPasswordCode;
import com.okhara.rating_system.model.auth.VerificationCode;
import io.lettuce.core.RedisURI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;
import java.util.List;

@Configuration
@Profile("prod")
@EnableRedisRepositories(
        keyspaceConfiguration = RedisProdConfig.AppKeyspaceConfig.class,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.OFF
)
@Slf4j
public class RedisProdConfig {
    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    @Value("${app.verification.verificationCodeExpiration}")
    private Duration verificationCodeExpiration;

    @Value("${app.password.resetPasswordCodeExpiration}")
    private Duration resetPasswordCodeExpiration;

    @PostConstruct
    public void logRedisConfig() {
        log.info("Redis URL: {}", redisUrl);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisURI redisURI = RedisURI.create(redisUrl);
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        log.info("--------------------------- REDIS HOST: {}", redisURI.getHost());
        configuration.setHostName(redisURI.getHost());

        log.info("--------------------------- REDIS PORT: {}", redisURI.getPort());
        configuration.setPort(redisURI.getPort());

        log.info("--------------------------- REDIS PASSWORD: {} - from env, {} - from uri", redisPassword,
                redisURI.getPassword());
        configuration.setPassword(RedisPassword.of(redisPassword));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

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
