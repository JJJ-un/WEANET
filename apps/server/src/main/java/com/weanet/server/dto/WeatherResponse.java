package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "날씨 응답 데이터")
public class WeatherResponse {
    @Schema(description = "날씨 상태", example = "Clear")
    private String weather;
    
    @Schema(description = "현재 기온 (섭씨)", example = "15.5")
    private double currentTemp;
    
    @Schema(description = "최고 기온 (섭씨)", example = "20.0")
    private double maxTemp;
    
    @Schema(description = "최저 기온 (섭씨)", example = "10.0")
    private double minTemp;
    
    @Schema(description = "강수 확률 (0.0 ~ 1.0)", example = "0.1")
    private double precipitationProbability; // 강수확률
}
