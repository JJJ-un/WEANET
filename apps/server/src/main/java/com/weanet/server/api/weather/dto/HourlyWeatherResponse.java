package com.weanet.server.api.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "시간대별 날씨 정보")
public class HourlyWeatherResponse {
    @Schema(description = "예보 날짜", example = "20240520")
    private String fcstDate;

    @Schema(description = "예보 시간", example = "1400")
    private String fcstTime;

    @Schema(description = "기온 (섭씨)", example = "18.5")
    private double temp;

    @Schema(description = "날씨 상태 (Clear, Cloudy, Overcast, Rain, Snow, Shower)", example = "Clear")
    private String weather;

    @Schema(description = "강수 확률 (0 ~ 100)", example = "20")
    private int precipitationProbability;
}
