package com.bloghub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogHubApplication.class, args);
    }
}
