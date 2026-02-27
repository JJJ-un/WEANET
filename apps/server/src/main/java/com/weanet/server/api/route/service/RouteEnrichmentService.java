package com.weanet.server.api.route.service;

import com.weanet.server.api.route.domain.TransportType;
import com.weanet.server.api.route.dto.response.RouteStepResponse;
import com.weanet.server.api.route.service.SubwayService;
import com.weanet.server.api.route.service.CongestionService;
import com.weanet.server.api.weather.service.WeatherService;
import com.weanet.server.api.route.dto.response.SubwayRealtimeResponse;
import com.weanet.server.api.weather.dto.WeatherResponse;
import com.weanet.server.api.common.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteEnrichmentService {

    private final WeatherService weatherService;
    private final CongestionService congestionService;
    private final SubwayService subwayService;
    private final KmaCoordinateConverter coordinateConverter;

    public void enrichRoute(List<RouteStepResponse> steps) {
        Map<String, WeatherResponse> weatherCache = new HashMap<>();
        for (RouteStepResponse step : steps) {
            enrichStepWithCache(step, weatherCache);
        }
    }

    private void enrichStepWithCache(RouteStepResponse step, Map<String, WeatherResponse> weatherCache) {
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(step.getLat(), step.getLng());
        String gridKey = grid.nx + "," + grid.ny;

        if (weatherCache.containsKey(gridKey)) {
            step.setWeather(weatherCache.get(gridKey));
        } else {
            WeatherResponse weather = weatherService.getCurrentWeatherByCoordinates(step.getLat(), step.getLng());
            step.setWeather(weather);
            weatherCache.put(gridKey, weather);
        }

        if (TransportType.SUBWAY.equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(
                step.getTransportType(), 
                step.getLineId(), 
                step.getStartStationId(),
                step.getStartStationName(),
                step.getLineName()
            ));
            
            List<SubwayRealtimeResponse> alerts = subwayService.getSubwayAlerts(step.getLineName());
            if (!alerts.isEmpty()) {
                step.setArrivalMessage(alerts.get(0).getArrivalMessage());
            }
        } else if (TransportType.BUS.equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(
                step.getTransportType(), 
                step.getLineId(), 
                step.getStartStationId(),
                step.getStartStationName(),
                step.getLineName()
            ));
        }
    }

    public void enrichStep(RouteStepResponse step) {
        enrichStepWithCache(step, new HashMap<>());
    }
}
