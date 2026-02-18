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
    private final RouteEnrichmentService enrichmentService;

    /**
     * 경로 검색: 외부 맵 서비스(Tmap)를 통해 경로 후보만 빠르게 가져옵니다.
     */
    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        return externalMapService.searchRoutes(
                request.getDepartureLat(), request.getDepartureLng(),
                request.getDestinationLat(), request.getDestinationLng());
    }

    /**
     * 경로 저장: 새로운 1:N 구조로 DB에 저장합니다.
     */
    @Transactional
    public RouteResponse createRoute(RouteSaveRequest request) {
        Route route = request.toEntity();
        Route savedRoute = routeRepository.save(route);
        return RouteResponse.from(savedRoute);
    }

    /**
     * 상세 조회: 저장된 경로의 실시간 상태를 보강하여 반환합니다.
     */
    public RouteDetailResponse getRouteDetail(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다. id=" + id));

        List<RouteStepResponse> stepResponses = route.getSteps().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 실시간 정보 보강
        enrichmentService.enrichRoute(stepResponses);

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
     * 경로 미리보기: 실시간 데이터와 통합 조언을 보강합니다.
     */
    public RouteSearchResponse enrichRoutePreview(RouteSearchResponse routeResponse) {
        enrichmentService.enrichRoute(routeResponse.getSteps());
        
        // 도메인 로직(조언 생성)을 위해 임시 객체나 정적 메소드 활용 가능
        // 여기서는 가독성을 위해 Route 엔티티의 비즈니스 로직을 활용하는 방향으로 개선
        Route tempRoute = Route.builder().build(); 
        routeResponse.setIntegratedAdvice(tempRoute.generateAdvice(routeResponse.getSteps()));
        
        return routeResponse;
    }

    private RouteStepResponse convertToResponse(RouteStep step) {
        return RouteStepResponse.builder()
                .sequence(step.getSequence())
                .transportType(step.getTransportType())
                .lineName(step.getLineName())
                .lineId(step.getLineId())
                .startStationName(step.getStartStationName())
                .startStationId(step.getStartStationId())
                .endStationName(step.getEndStationName())
                .endStationId(step.getEndStationId())
                .lat(step.getLat())
                .lng(step.getLng())
                .build();
    }
}
