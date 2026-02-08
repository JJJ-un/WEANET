package com.weanet.server.service;

import com.weanet.server.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public WeatherResponse getWeather(String city) {
        // API Key가 설정되지 않은 경우 Mock 데이터 반환
        if ("YOUR_OPENWEATHERMAP_API_KEY".equals(apiKey) || apiKey.isEmpty()) {
            return getMockWeather();
        }

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("list")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
                Map<String, Object> firstForecast = list.get(0);
                
                Map<String, Object> main = (Map<String, Object>) firstForecast.get("main");
                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) firstForecast.get("weather");
                String weatherDescription = (String) weatherList.get(0).get("main");
                
                double currentTemp = ((Number) main.get("temp")).doubleValue();
                double minTemp = ((Number) main.get("temp_min")).doubleValue();
                double maxTemp = ((Number) main.get("temp_max")).doubleValue();
                double pop = firstForecast.containsKey("pop") ? ((Number) firstForecast.get("pop")).doubleValue() : 0.0;

                return WeatherResponse.builder()
                        .weather(weatherDescription)
                        .currentTemp(currentTemp)
                        .minTemp(minTemp)
                        .maxTemp(maxTemp)
                        .precipitationProbability(pop)
                        .build();
            }
        } catch (Exception e) {
            // 에러 발생 시 로그를 남기고 Mock 데이터 혹은 에러 응답
            System.err.println("Error fetching weather data: " + e.getMessage());
        }

        return getMockWeather();
    }

    private WeatherResponse getMockWeather() {
        return WeatherResponse.builder()
                .weather("Cloudy (Mock)")
                .currentTemp(12.0)
                .maxTemp(18.0)
                .minTemp(8.0)
                .precipitationProbability(0.3)
                .build();
    }
}