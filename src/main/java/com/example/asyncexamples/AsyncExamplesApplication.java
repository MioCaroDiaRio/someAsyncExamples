package com.example.asyncexamples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AsyncExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncExamplesApplication.class, args);
    }
}
