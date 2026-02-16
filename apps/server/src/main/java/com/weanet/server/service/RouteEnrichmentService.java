package com.weanet.server.service;

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
            WeatherResponse weather = weatherService.getWeatherByCoordinates(step.getLat(), step.getLng());
            step.setWeather(weather);
            weatherCache.put(gridKey, weather);
        }

        // 2. 교통수단별 혼잡도 및 알림 보강
        if ("SUBWAY".equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(step.getTransportType(), step.getLineId(), step.getStartStationId()));
            
            List<SubwayRealtimeResponse> alerts = subwayService.getSubwayAlerts(step.getLineName());
            if (!alerts.isEmpty()) {
                step.setArrivalMessage(alerts.get(0).getArrivalMessage());
            }
        } else if ("BUS".equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(step.getTransportType(), step.getLineId(), step.getStartStationId()));
        }
    }

    /**
     * 기존의 단일 보강 메서드 (필요 시 유지)
     */
    public void enrichStep(RouteStepResponse step) {
        enrichStepWithCache(step, new HashMap<>());
    }

    /**
     * 보강된 데이터를 바탕으로 통합 조언 메시지를 생성합니다.
     */
    public String generateIntegratedAdvice(List<RouteStepResponse> steps) {
        if (steps == null || steps.isEmpty()) return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";

        boolean isRainy = steps.stream()
                .filter(s -> s.getWeather() != null && s.getWeather().getAdvice() != null)
                .anyMatch(s -> s.getWeather().getAdvice().contains("비"));
        
        boolean isCongested = steps.stream()
                .filter(s -> s.getCongestion() != null)
                .anyMatch(s -> "혼잡".equals(s.getCongestion()));
        
        boolean isDelayed = steps.stream()
                .filter(s -> s.getArrivalMessage() != null)
                .anyMatch(s -> s.getArrivalMessage().contains("지연") || s.getArrivalMessage().contains("장애") || s.getArrivalMessage().contains("점검"));

        if (isDelayed) return "현재 이용하실 지하철 노선에 공식 지연/장애 공지가 있습니다. 상세 정보를 확인해 주세요! ⚠️🚇";
        if (isRainy && isCongested) return "현재 경로에 비가 오고 대중교통이 매우 혼잡합니다. 평소보다 15분 일찍 출발하시고 우산을 꼭 챙기세요! ☔️🔴";
        if (isRainy) return "경로 구간에 비 소식이 있습니다. 이동 시 우산을 챙기시고 발밑 조심하세요! ☔️";
        if (isCongested) return "현재 이용하실 노선이 많이 혼잡합니다. 여유가 있다면 다음 열차/버스를 이용해 보세요. 🔴";
        
        return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";
    }
}
