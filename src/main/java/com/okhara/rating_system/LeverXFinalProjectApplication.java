package com.okhara.rating_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.okhara.rating_system.repository.jpa")
@EnableRedisRepositories(basePackages = "com.okhara.rating_system.repository.redis")
public class LeverXFinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeverXFinalProjectApplication.class, args);
	}

}
