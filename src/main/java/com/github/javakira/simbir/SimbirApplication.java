package com.github.javakira.simbir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories({"com.github.javakira.simbir.account", "com.github.javakira.simbir.transport"})
public class SimbirApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimbirApplication.class, args);
    }
}
