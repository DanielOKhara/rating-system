package com.okhara.rating_system.configuration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@Configuration
@EnableRedisRepositories(
        keyspaceConfiguration = RedisConfiguration.AppKeyspaceConfig.class,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
)
@Profile("dev")
public class RedisConfiguration extends AbstractRedisConfig{

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());

        return new JedisConnectionFactory(configuration);
    }
}
