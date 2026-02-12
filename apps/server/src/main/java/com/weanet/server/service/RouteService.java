package com.weanet.server.service;

import com.weanet.server.domain.Route;
import com.weanet.server.domain.RouteStep;
import com.weanet.server.dto.*;
import com.weanet.server.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final ExternalMapService externalMapService;
    private final WeatherService weatherService;
    private final CongestionService congestionService;

    /**
     * 경로 검색: 외부 맵 서비스(Tmap)를 통해 경로 후보만 빠르게 가져옵니다.
     */
    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        // 1. 외부 맵 서비스 호출 (순수 Tmap 경로 데이터)
        return externalMapService.searchRoutes(
                request.getDepartureLat(), request.getDepartureLng(),
                request.getDestinationLat(), request.getDestinationLng());
    }

    /**
     * 경로 저장: 새로운 1:N 구조(Route + RouteStep)로 DB에 저장합니다.
     */
    @Transactional
    public RouteResponse createRoute(RouteSaveRequest request) {
        Route route = Route.builder()
                .name(request.getName())
                .departureName(request.getDepartureName())
                .departureLat(request.getDepartureLat())
                .departureLng(request.getDepartureLng())
                .destinationName(request.getDestinationName())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .totalTime(request.getTotalTime())
                .totalFare(request.getTotalFare())
                .transferCount(request.getTransferCount())
                .build();

        if (request.getSteps() != null) {
            for (RouteStepSaveRequest stepReq : request.getSteps()) {
                RouteStep step = RouteStep.builder()
                        .route(route)
                        .sequence(stepReq.getSequence())
                        .transportType(stepReq.getTransportType())
                        .lineName(stepReq.getLineName())
                        .lineId(stepReq.getLineId())
                        .startStationName(stepReq.getStartStationName())
                        .startStationId(stepReq.getStartStationId())
                        .endStationName(stepReq.getEndStationName())
                        .endStationId(stepReq.getEndStationId())
                        .lat(stepReq.getLat())
                        .lng(stepReq.getLng())
                        .build();
                route.addStep(step);
            }
        }

        Route savedRoute = routeRepository.save(route);
        return RouteResponse.from(savedRoute);
    }

    /**
     * 상세 조회: 저장된 경로의 각 구간별 실시간 상태를 포함하여 반환합니다.
     */
    public RouteDetailResponse getRouteDetail(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다. id=" + id));

        List<RouteStepResponse> stepResponses = route.getSteps().stream()
                .map(step -> {
                    RouteStepResponse res = RouteStepResponse.builder()
                            .sequence(step.getSequence())
                            .transportType(step.getTransportType())
                            .lineName(step.getLineName())
                            .startStationName(step.getStartStationName())
                            .endStationName(step.getEndStationName())
                            .build();
                    enrichStepWithRealTimeData(res, step.getLat(), step.getLng(), step.getLineId(), step.getStartStationId());
                    return res;
                })
                .collect(Collectors.toList());

        return RouteDetailResponse.builder()
                .name(route.getName())
                .departureName(route.getDepartureName())
                .destinationName(route.getDestinationName())
                .totalTime(route.getTotalTime())
                .totalFare(route.getTotalFare())
                .steps(stepResponses)
                .build();
    }

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(RouteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    /**
     * 경로 미리보기: 선택한 경로에 대해 실시간 데이터(날씨, 혼잡도)와 통합 조언을 채웁니다.
     */
    public RouteSearchResponse enrichRoutePreview(RouteSearchResponse route) {
        for (RouteStepResponse step : route.getSteps()) {
            enrichStepWithRealTimeData(step, step.getLat(), step.getLng(), step.getLineId(), step.getStartStationId());
        }

        // 통합 조언 생성 (날씨와 혼잡도 기반)
        route.setIntegratedAdvice(generateAdvice(route.getSteps()));
        
        return route;
    }

    private String generateAdvice(List<RouteStepResponse> steps) {
        boolean isRainy = steps.stream()
                .anyMatch(s -> s.getWeather() != null && s.getWeather().getAdvice() != null && s.getWeather().getAdvice().contains("비"));
        boolean isCongested = steps.stream()
                .anyMatch(s -> "혼잡".equals(s.getCongestion()));

        if (isRainy && isCongested) {
            return "현재 경로에 비가 오고 대중교통이 매우 혼잡합니다. 평소보다 15분 일찍 출발하시고 우산을 꼭 챙기세요! ☔️🔴";
        } else if (isRainy) {
            return "경로 구간에 비 소식이 있습니다. 이동 시 우산을 챙기시고 발밑 조심하세요! ☔️";
        } else if (isCongested) {
            return "현재 이용하실 노선이 많이 혼잡합니다. 여유가 있다면 다음 열차/버스를 이용해 보세요. 🔴";
        }
        
        return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";
    }

    // Helper: 구간에 실시간 데이터를 채웁니다 (좌표/ID 기반)
    private void enrichStepWithRealTimeData(RouteStepResponse step, double lat, double lng, String lineId, String stationId) {
        step.setWeather(weatherService.getWeatherByCoordinates(lat, lng));
        if ("SUBWAY".equals(step.getTransportType()) || "BUS".equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(step.getTransportType(), lineId, stationId));
        }
    }
}