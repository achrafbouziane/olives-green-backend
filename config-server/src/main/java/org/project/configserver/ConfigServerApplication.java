package org.project.configserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableConfigServer
@Slf4j
public class ConfigServerApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ConfigServerApplication.class, args);
        log.info("Config Server running on port: {}", context.getEnvironment().getProperty("server.port"));
    }
}

