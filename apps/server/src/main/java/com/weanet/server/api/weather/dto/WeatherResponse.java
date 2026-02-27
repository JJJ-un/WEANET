package com.weanet.server.api.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@Schema(description = "날씨 정보 응답 DTO")
public record WeatherResponse(
    @Schema(description = "현재 날씨 상태 (예: Clear, Rain)", example = "Rain")
    String weather,

    @Schema(description = "현재 기온 (섭씨)", example = "24.5")
    double currentTemp,

    @Schema(description = "최고 기온 (섭씨)", example = "30.0")
    double maxTemp,

    @Schema(description = "최저 기온 (섭씨)", example = "18.0")
    double minTemp,

    @Schema(description = "강수 확률 (0.0 ~ 1.0)", example = "0.8")
    double precipitationProbability,

    @Schema(description = "이동 조언 메시지", example = "비가 오니 우산을 챙기세요!")
    String advice,

    @Schema(description = "시간대별 예보 리스트")
    List<HourlyWeatherResponse> hourlyForecast
) {
}
