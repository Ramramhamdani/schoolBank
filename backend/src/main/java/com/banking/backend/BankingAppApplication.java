package com.banking.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.banking.backend.repository")
@ComponentScan(basePackages = {"com.banking.backend"})
public class BankingAppApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(BankingAppApplication.class, args);
    }
}