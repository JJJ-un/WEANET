package com.weanet.server.api.weather;

import com.weanet.server.api.weather.dto.HourlyWeatherResponse;
import com.weanet.server.api.weather.dto.KmaWeatherApiResponse;
import com.weanet.server.api.weather.dto.WeatherResponse;
import com.weanet.server.api.common.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    private final KmaCoordinateConverter coordinateConverter;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${kma.api.key}")
    private String kmaApiKey;

    @Value("${kma.api.url}")
    private String kmaApiUrl;

    public WeatherResponse getWeather(String city) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("q", city)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .queryParam("lang", "kr")
                    .build().toUriString();

            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (Exception e) {
            log.error("Error fetching weather for city {}: {}", city, e.getMessage());
            return WeatherResponse.builder()
                    .weather("Unknown")
                    .advice("날씨 정보를 불러올 수 없습니다.")
                    .build();
        }
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        return getCurrentWeatherByCoordinates(lat, lng);
    }

    public WeatherResponse getCurrentWeatherByCoordinates(double lat, double lng) {
        try {
            KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(lat, lng);
            
            String url = UriComponentsBuilder.fromUriString(kmaApiUrl)
                    .queryParam("serviceKey", kmaApiKey)
                    .queryParam("numOfRows", 100)
                    .queryParam("pageNo", 1)
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", "20240320") // 임시 하드코딩
                    .queryParam("base_time", "0500")
                    .queryParam("nx", grid.nx)
                    .queryParam("ny", grid.ny)
                    .build().toUriString();

            KmaWeatherApiResponse response = restTemplate.getForObject(url, KmaWeatherApiResponse.class);
            return parseKmaResponse(response);
        } catch (Exception e) {
            log.error("Error fetching KMA weather: {}", e.getMessage());
            return WeatherResponse.builder()
                    .weather("Unknown")
                    .advice("기상청 정보를 불러올 수 없습니다.")
                    .build();
        }
    }

    private WeatherResponse parseKmaResponse(KmaWeatherApiResponse response) {
        // 실제 파싱 로직 (생략/단축)
        return WeatherResponse.builder()
                .weather("Clear")
                .currentTemp(20.5)
                .advice("맑은 날씨입니다. 가벼운 옷차림을 추천해요!")
                .build();
    }

    public double[] getCoordinates(String keyword) {
        return new double[]{37.5665, 126.9780};
    }
}
