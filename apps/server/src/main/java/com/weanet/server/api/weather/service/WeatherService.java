package com.weanet.server.api.weather.service;

import com.weanet.server.api.external.service.ExternalMapService;
import com.weanet.server.api.weather.domain.WeatherForecasts;
import com.weanet.server.api.weather.dto.response.KmaWeatherApiResponse;
import com.weanet.server.api.weather.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ExternalMapService externalMapService;
    private final KmaWeatherClient kmaWeatherClient;
    private final WeatherAdviceGenerator adviceGenerator;

    public WeatherResponse getWeather(String city) {
        double[] coords = externalMapService.getCoordinates(city);
        return getWeatherByCoordinates(coords[0], coords[1]);
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        List<KmaWeatherApiResponse.Item> items = kmaWeatherClient.fetchWeatherData(lat, lng, 400);
        return buildWeatherResponse(items, true);
    }

    public WeatherResponse getCurrentWeatherByCoordinates(double lat, double lng) {
        List<KmaWeatherApiResponse.Item> items = kmaWeatherClient.fetchWeatherData(lat, lng, 20);
        return buildWeatherResponse(items, false);
    }

    private WeatherResponse buildWeatherResponse(List<KmaWeatherApiResponse.Item> items, boolean includeHourly) {
        WeatherForecasts forecasts = WeatherForecasts.from(items);
        WeatherForecasts.ForecastUnit current = forecasts.findCurrent();
        
        String advice = (current != null) 
                ? adviceGenerator.generateAdvice(current.getWeatherStatus(), current.getTemp(), current.getPop())
                : adviceGenerator.generateEmptyAdvice();

        return WeatherResponse.of(forecasts, advice, includeHourly);
    }
}
