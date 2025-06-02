package com.uade.cookitbackend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class CookitBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookitBackendApplication.class, args);
    }

}
