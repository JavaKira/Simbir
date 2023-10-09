package com.github.javakira.simbir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SimbirApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimbirApplication.class, args);
    }
}
