package com.weanet.server.api.weather.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weanet.server.api.weather.domain.WeatherForecasts;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "날씨 응답 데이터")
public record WeatherResponse(
    @Schema(description = "날씨 상태", example = "Clear")
    String weather,
    
    @Schema(description = "현재 기온 (섭씨)", example = "15.5")
    double currentTemp,
    
    @Schema(description = "최고 기온 (섭씨)", example = "20.0")
    double maxTemp,
    
    @Schema(description = "최저 기온 (섭씨)", example = "10.0")
    double minTemp,
    
    @Schema(description = "강수 확률 (0 ~ 100)", example = "10")
    int precipitationProbability,

    @Schema(description = "날씨 맞춤형 조언", example = "비 소식이 있어요. 우산 꼭 챙기세요! ☂️")
    String advice,

    @Schema(description = "시간대별 예보 리스트")
    List<HourlyWeatherResponse> hourlyForecast
) {
    public static WeatherResponse of(WeatherForecasts forecasts, String advice, boolean includeHourly) {
        WeatherForecasts.ForecastUnit current = forecasts.findCurrent();
        if (current == null) {
            return WeatherResponse.builder()
                    .weather("Unknown")
                    .advice(advice)
                    .build();
        }

        WeatherResponseBuilder builder = WeatherResponse.builder()
                .weather(current.getWeatherStatus())
                .currentTemp(current.getTemp())
                .precipitationProbability(current.getPop())
                .minTemp(forecasts.calculateMinTemp(current.getTemp() - 2))
                .maxTemp(forecasts.calculateMaxTemp(current.getTemp() + 5))
                .advice(advice);

        if (includeHourly) {
            builder.hourlyForecast(forecasts.toHourlyResponses());
        }

        return builder.build();
    }
}
