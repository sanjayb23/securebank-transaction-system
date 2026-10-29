package com.securebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecureBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureBankApplication.class, args);
    }

}