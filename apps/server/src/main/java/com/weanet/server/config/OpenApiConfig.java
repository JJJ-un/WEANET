package com.weanet.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DDubuk 날씨 API 문서")
                        .description("현재 날씨 및 예보 정보를 제공하는 API입니다.")
                        .version("v0.0.1"));
    }
}
