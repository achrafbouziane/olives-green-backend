package org.project.gatewayservice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GatewayServiceApplication.class);
        log.info("\n✅ GATEWAY SERVICE STARTED\n" +
                        "------------------------------------------------------\n" +
                        "🌐 Gateway URL: http://127.0.0.1:{}\n" +
                        "------------------------------------------------------",
                context.getEnvironment().getProperty("server.port"));
    }
}