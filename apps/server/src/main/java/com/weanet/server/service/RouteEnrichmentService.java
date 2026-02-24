package com.weanet.server.service;

import com.weanet.server.domain.TransportType;
import com.weanet.server.dto.RouteStepResponse;
import com.weanet.server.dto.SubwayRealtimeResponse;
import com.weanet.server.dto.WeatherResponse;
import com.weanet.server.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 경로 데이터에 실시간 정보(날씨, 혼잡도, 지연 등)를 입혀주는 전담 서비스
 */
@Service
@RequiredArgsConstructor
public class RouteEnrichmentService {

    private final WeatherService weatherService;
    private final CongestionService congestionService;
    private final SubwayService subwayService;
    private final KmaCoordinateConverter coordinateConverter;

    /**
     * 경로 검색 결과나 저장된 경로 상세 정보에 실시간 데이터를 보강합니다.
     */
    public void enrichRoute(List<RouteStepResponse> steps) {
        // 동일 경로 내 중복된 격자 좌표의 날씨 조회를 방지하기 위한 로컬 캐시
        Map<String, WeatherResponse> weatherCache = new HashMap<>();

        for (RouteStepResponse step : steps) {
            enrichStepWithCache(step, weatherCache);
        }
    }

    /**
     * 캐시를 활용하여 단일 구간(Step)에 실시간 데이터를 채웁니다.
     */
    private void enrichStepWithCache(RouteStepResponse step, Map<String, WeatherResponse> weatherCache) {
        // 1. 날씨 정보 보강 (격자 좌표 기반 캐싱 적용)
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(step.getLat(), step.getLng());
        String gridKey = grid.nx + "," + grid.ny;

        if (weatherCache.containsKey(gridKey)) {
            step.setWeather(weatherCache.get(gridKey));
        } else {
            WeatherResponse weather = weatherService.getCurrentWeatherByCoordinates(step.getLat(), step.getLng());
            step.setWeather(weather);
            weatherCache.put(gridKey, weather);
        }

        // 2. 교통수단별 혼잡도 및 알림 보강
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

    /**
     * 기존의 단일 보강 메서드 (필요 시 유지)
     */
    public void enrichStep(RouteStepResponse step) {
        enrichStepWithCache(step, new HashMap<>());
    }
}
