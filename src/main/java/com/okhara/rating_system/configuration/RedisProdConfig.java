package com.okhara.rating_system.configuration;

import io.lettuce.core.RedisURI;
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
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@Configuration
@Profile("prod")
@EnableRedisRepositories(
        keyspaceConfiguration = RedisProdConfig.AppKeyspaceConfig.class,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.OFF
)
@Slf4j
public class RedisProdConfig extends AbstractRedisConfig{
    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisURI redisURI = RedisURI.create(redisUrl);
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisURI.getHost());
        configuration.setPort(redisURI.getPort());
        configuration.setPassword(RedisPassword.of(redisPassword));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }
}
