package com.weanet.server.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    /**
     * Spring Boot에서 자동 구성된 RestTemplateBuilder를 주입받아 사용합니다.
     * 이를 통해 자동 구성된 인터셉터, 관측성 설정 등을 모두 유지할 수 있습니다.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5)) // 연결 타임아웃 5초
                .setReadTimeout(Duration.ofSeconds(5))    // 읽기 타임아웃 5초
                .build();
    }
}
