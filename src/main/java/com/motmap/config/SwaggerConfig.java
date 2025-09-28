package com.motmap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI motmapOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MOTMAP API")
                        .description("카카오맵 기반 맛집 저장 및 리뷰 서비스 API")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MOTMAP Team")
                                .email("contact@motmap.com")
                                .url("https://github.com/motmap/motmap"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.motmap.com")
                                .description("운영 서버")
                ));
    }
}
