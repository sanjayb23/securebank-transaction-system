package com.securebank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SecureBank API")
                        .version("1.0")
                        .description("RESTful Banking Transaction API")
                        .contact(new Contact()
                                .name("SecureBank Team")
                                .email("support@securebank.com")));
    }
}