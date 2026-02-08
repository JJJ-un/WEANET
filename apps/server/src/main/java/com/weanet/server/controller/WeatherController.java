package com.weanet.server.controller;

import com.weanet.server.dto.WeatherResponse;
import com.weanet.server.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "날씨 관련 API")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    @Operation(summary = "현재 날씨 조회", description = "도시 이름을 기반으로 현재 기온, 최고/최저 기온, 강수 확률을 조회합니다.")
    public WeatherResponse getWeather(
            @Parameter(description = "도시 이름 (영문)", example = "Seoul")
            @RequestParam(defaultValue = "Seoul") String city) {
        return weatherService.getWeather(city);
    }
}
