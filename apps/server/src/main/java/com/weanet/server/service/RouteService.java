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
     * 경로 검색: 외부 맵 서비스를 통해 경로 후보를 가져오고, 각 구간에 실시간 정보를 결합합니다.
     */
    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        // 1. 외부 맵 서비스 호출 (좌표는 우선 Mock으로 0,0 처리)
        List<RouteSearchResponse> searchResults = externalMapService.searchRoutes(0, 0, 0, 0);

        // 2. 각 경로의 구간별 실시간 정보(날씨, 혼잡도) 결합
        for (RouteSearchResponse route : searchResults) {
            for (RouteStepResponse step : route.getSteps()) {
                enrichStepWithRealTimeData(step);
            }
        }
        return searchResults;
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

    // Helper: 구간에 실시간 데이터를 채웁니다 (검색용)
    private void enrichStepWithRealTimeData(RouteStepResponse step) {
        // 검색 단계에서는 좌표 정보가 없을 수 있으므로 기본값 혹은 임의값 사용
        enrichStepWithRealTimeData(step, 37.5, 127.0, "MOCK_LINE", "MOCK_STATION");
    }

    // Helper: 구간에 실시간 데이터를 채웁니다 (좌표/ID 기반)
    private void enrichStepWithRealTimeData(RouteStepResponse step, double lat, double lng, String lineId, String stationId) {
        step.setWeather(weatherService.getWeatherByCoordinates(lat, lng));
        if ("SUBWAY".equals(step.getTransportType()) || "BUS".equals(step.getTransportType())) {
            step.setCongestion(congestionService.getCongestion(lineId, stationId));
        }
    }
}