package com.weanet.server.api.weather.domain;

import com.weanet.server.api.weather.dto.response.HourlyWeatherResponse;
import com.weanet.server.api.weather.dto.response.KmaWeatherApiResponse;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherForecasts {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final List<ForecastUnit> forecasts;
    private final DoubleSummaryStatistics todayStats;

    private WeatherForecasts(List<ForecastUnit> forecasts) {
        // 방어적 복사(Defensive Copy)를 통해 외부 리스트의 조작으로부터 데이터를 보호합니다.
        // List.copyOf()는 Java 10+ 에서 제공하는 불변 리스트 생성 메서드입니다.
        this.forecasts = List.copyOf(forecasts);
        
        // "오늘"의 기준을 한 번만 정의 (캡슐화: 시간의 기준점 통일)
        LocalDate today = LocalDate.now(KST);
        
        // ForecastUnit에게 "오늘 데이터니?"라고 물어보며 통계 계산 (가독성 향상)
        this.todayStats = forecasts.stream()
                .filter(f -> f.isToday(today))
                .mapToDouble(ForecastUnit::getTemp)
                .summaryStatistics();
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

    public double calculateMinTemp(double currentTemp) {
        return todayStats.getCount() > 0 ? todayStats.getMin() : currentTemp;
    }

    public double calculateMaxTemp(double currentTemp) {
        return todayStats.getCount() > 0 ? todayStats.getMax() : currentTemp;
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

        // 오늘 날짜인지 판단하는 로직을 객체 내부로 캡슐화
        public boolean isToday(LocalDate today) {
            return this.dateTime.toLocalDate().isEqual(today);
        }
    }
}
