package com.okhara.rating_system.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPIDescription(){
        Server localhostServer = new Server();
        localhostServer.setUrl("http://localhost:8080");
        localhostServer.setDescription("Local env");

        Server productionServer = new Server();
        productionServer.setUrl("http://some.prod.url");
        productionServer.setDescription("Production env");

        Contact contact = new Contact();
        contact.setName("Uladzislau Petrachkou");
        contact.setEmail("upetrachkou@gmail.com");
        contact.setUrl("http://some.url");

        License license = new License()
                .name("GNU AGPLv3")
                .url("https://choosealicense.com/license/agpl-3.0/");

        Info info = new Info()
                .title("LeverX final project. Petrachkou")
                .version("1.0")
                .contact(contact)
                .description("API selling-platform for game objects. Sellers rating-system")
                .termsOfService("http://some.terms.url")
                .license(license);

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("BearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localhostServer, productionServer))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .schemaRequirement("BearerAuth", securityScheme);
    }
}
