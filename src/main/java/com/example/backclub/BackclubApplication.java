package com.example.backclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.backclub", "domain", "repository"})
public class BackclubApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackclubApplication.class, args);
    }

}
