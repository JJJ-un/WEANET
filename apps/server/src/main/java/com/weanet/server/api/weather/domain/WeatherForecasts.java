package com.weanet.server.api.weather.domain;

import com.weanet.server.api.weather.dto.response.HourlyWeatherResponse;
import com.weanet.server.api.weather.dto.response.KmaWeatherApiResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherForecasts {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private final List<ForecastUnit> forecasts;

    private WeatherForecasts(List<ForecastUnit> forecasts) {
        this.forecasts = forecasts;
    }

    public static WeatherForecasts from(List<KmaWeatherApiResponse.Item> items) {
        List<ForecastUnit> units = items.stream()
                .collect(Collectors.groupingBy(item -> item.getFcstDate() + item.getFcstTime(), 
                        TreeMap::new, Collectors.toList()))
                .values().stream()
                .map(ForecastUnit::new)
                .collect(Collectors.toList());
        return new WeatherForecasts(units);
    }

    public ForecastUnit findCurrent() {
        LocalDateTime now = LocalDateTime.now(KST);
        
        return forecasts.stream()
                .filter(f -> !f.dateTime.isBefore(now)) // 현재 혹은 가장 가까운 미래
                .findFirst()
                .orElse(forecasts.isEmpty() ? null : forecasts.get(0));
    }

    public double calculateMinTemp(double fallback) {
        LocalDateTime today = LocalDateTime.now(KST).withHour(0).withMinute(0);
        return forecasts.stream()
                .filter(f -> f.dateTime.toLocalDate().isEqual(today.toLocalDate()))
                .mapToDouble(f -> f.temp)
                .min()
                .orElse(fallback);
    }

    public double calculateMaxTemp(double fallback) {
        LocalDateTime today = LocalDateTime.now(KST).withHour(0).withMinute(0);
        return forecasts.stream()
                .filter(f -> f.dateTime.toLocalDate().isEqual(today.toLocalDate()))
                .mapToDouble(f -> f.temp)
                .max()
                .orElse(fallback);
    }

    public List<HourlyWeatherResponse> toHourlyResponses() {
        return forecasts.stream()
                .map(f -> HourlyWeatherResponse.of(
                        f.dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        f.dateTime.format(DateTimeFormatter.ofPattern("HHmm")),
                        f.temp, f.weatherStatus, f.pop))
                .collect(Collectors.toList());
    }

    @Getter
    public static class ForecastUnit {
        private final LocalDateTime dateTime;
        private final double temp;
        private final int pop;
        private final String weatherStatus;

        private ForecastUnit(List<KmaWeatherApiResponse.Item> items) {
            // 1. 시간 파싱 (yyyyMMddHHmm)
            KmaWeatherApiResponse.Item first = items.get(0);
            this.dateTime = LocalDateTime.parse(first.getFcstDate() + first.getFcstTime(), 
                    DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            
            // 2. 카테고리별 데이터 맵핑 (선언적 추출을 위해 Map으로 변환)
            Map<String, String> values = items.stream()
                    .collect(Collectors.toMap(KmaWeatherApiResponse.Item::getCategory, 
                            KmaWeatherApiResponse.Item::getFcstValue, (v1, v2) -> v1));

            // 3. 필드 할당 (기본값 처리 포함)
            this.temp = Double.parseDouble(values.getOrDefault("TMP", "0.0"));
            this.pop = Integer.parseInt(values.getOrDefault("POP", "0"));
            this.weatherStatus = WeatherStatus.interpret(
                    values.getOrDefault("SKY", "1"),
                    values.getOrDefault("PTY", "0")
            );
        }
    }
}
