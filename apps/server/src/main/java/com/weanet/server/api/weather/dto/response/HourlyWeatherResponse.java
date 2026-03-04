package com.weanet.server.api.weather.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "시간대별 날씨 정보")
public record HourlyWeatherResponse(
    @Schema(description = "예보 날짜", example = "20240520")
    String fcstDate,

    @Schema(description = "예보 시간", example = "1400")
    String fcstTime,

    @Schema(description = "기온 (섭씨)", example = "18.5")
    double temp,

    @Schema(description = "날씨 상태 (Clear, Cloudy, Overcast, Rain, Snow, Shower)", example = "Clear")
    String weather,

    @Schema(description = "강수 확률 (0 ~ 100)", example = "20")
    int precipitationProbability
) {
    public static HourlyWeatherResponse of(String date, String time, double temp, String weather, int pop) {
        return HourlyWeatherResponse.builder()
                .fcstDate(date)
                .fcstTime(time)
                .temp(temp)
                .weather(weather)
                .precipitationProbability(pop)
                .build();
    }
}
