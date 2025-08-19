package Ayrotek.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AyrotekApplication {   

    public static void main(String[] args) {
        SpringApplication.run(AyrotekApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vehicle and Driver Registration API")
                        .version("1.0.0")
                        .description("A Spring Boot API for registering and verifying vehicle and driver information.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
