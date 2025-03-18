package com.okhara.rating_system.configuration;

import com.okhara.rating_system.model.auth.RefreshToken;
import com.okhara.rating_system.model.auth.ResetPasswordCode;
import com.okhara.rating_system.model.auth.VerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableRedisRepositories(
        keyspaceConfiguration = RedisConfiguration.AppKeyspaceConfig.class,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
)
@Profile("dev")
public class RedisConfiguration {

    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());

        return new JedisConnectionFactory(configuration);
    }

    public class AppKeyspaceConfig extends KeyspaceConfiguration{
        private static final String REFRESH_TOKEN_KEYSPACE = "refresh_tokens";
        private static final String VERIFICATION_CODE_KEYSPACE = "verification_codes";
        private static final String RESET_PASSWORD_CODE_KEYSPACE = "reset_password_codes";


        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration(){
            KeyspaceSettings refreshTokenSettings = new KeyspaceSettings(RefreshToken.class, REFRESH_TOKEN_KEYSPACE);
            refreshTokenSettings.setTimeToLive(refreshTokenExpiration.getSeconds());

            KeyspaceSettings verificationCodeSettings = new KeyspaceSettings(
                    VerificationCode.class, VERIFICATION_CODE_KEYSPACE);
            verificationCodeSettings.setTimeToLive(Duration.ofHours(24).getSeconds());

            KeyspaceSettings resetPasswordCodeSettings = new KeyspaceSettings(
                    ResetPasswordCode.class, RESET_PASSWORD_CODE_KEYSPACE);
            resetPasswordCodeSettings.setTimeToLive(Duration.ofMinutes(5).getSeconds());

            return List.of(refreshTokenSettings, verificationCodeSettings, resetPasswordCodeSettings);
        }
    }
}
