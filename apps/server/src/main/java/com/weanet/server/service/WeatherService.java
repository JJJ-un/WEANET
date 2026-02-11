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

    /**
     * 도시 이름을 기반으로 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeather(String city) {
        if (isMockMode()) {
            return getMockWeather(12.0, 0.3);
        }

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        return fetchWeatherData(url);
    }

    /**
     * 좌표(위도, 경도)를 기반으로 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        if (isMockMode()) {
            return getMockWeather(15.0 + (lat % 5), 0.1 + (lng % 0.5));
        }

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("lat", lat)
                .queryParam("lon", lng)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        return fetchWeatherData(url);
    }

    private boolean isMockMode() {
        return "YOUR_OPENWEATHERMAP_API_KEY".equals(apiKey) || apiKey.isEmpty();
    }

    private WeatherResponse fetchWeatherData(String url) {
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("list")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
                if (!list.isEmpty()) {
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
                            .advice(generateAdvice(weatherDescription, currentTemp, pop))
                            .build();
                }
            }
        } catch (Exception e) {
            // 실제 서비스 시에는 로그 프레임워크(SLF4J 등)를 사용하는 것이 좋으나 우선 콘솔 로그 유지
            System.err.println("Error fetching weather data from external API: " + e.getMessage());
        }
        return getMockWeather(12.0, 0.3); // 에러 발생 시 최후의 수단으로 Mock 반환
    }

    private String generateAdvice(String weather, double temp, double pop) {
        if (pop > 0.5 || weather.toLowerCase().contains("rain")) {
            return "비 소식이 있어요. 우산 꼭 챙기세요! ☂️";
        }
        if (temp < 5) {
            return "날씨가 많이 추워요. 두꺼운 외투를 입으세요! 🧥";
        }
        if (temp < 15) {
            return "조금 쌀쌀할 수 있어요. 가벼운 겉옷을 추천해요. 🧣";
        }
        if (temp > 28) {
            return "날씨가 더워요! 시원한 옷차림과 수분 섭취 잊지 마세요. ☀️";
        }
        return "활동하기 적당한 날씨예요. 기분 좋은 하루 되세요! 😊";
    }

    private WeatherResponse getMockWeather(double temp, double pop) {
        return WeatherResponse.builder()
                .weather("Cloudy (Mock)")
                .currentTemp(temp)
                .maxTemp(temp + 5)
                .minTemp(temp - 5)
                .precipitationProbability(pop)
                .advice(generateAdvice("Cloudy", temp, pop))
                .build();
    }
}