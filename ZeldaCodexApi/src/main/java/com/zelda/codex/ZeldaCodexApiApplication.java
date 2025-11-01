package com.zelda.codex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZeldaCodexApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeldaCodexApiApplication.class, args);
    }
}