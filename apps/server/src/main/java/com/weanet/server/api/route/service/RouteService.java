package com.weanet.server.api.route.service;

import com.weanet.server.api.external.service.ExternalMapService;
import com.weanet.server.api.route.domain.Route;
import com.weanet.server.api.route.dto.request.*;
import com.weanet.server.api.route.dto.response.*;
import com.weanet.server.api.route.repository.RouteRepository;
import com.weanet.server.global.error.exception.BusinessException;
import com.weanet.server.global.error.exception.ErrorCode;
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
    private final RouteAdviceService adviceService;

    /**
     * 경로 검색: 명칭을 위도/경도로 변환한 뒤 Tmap을 통해 경로 후보를 가져옵니다.
     */
    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        // 1. 출발지 명칭 -> 좌표 변환
        double[] startCoords = externalMapService.getCoordinates(request.getDepartureName());
        
        // 2. 도착지 명칭 -> 좌표 변환
        double[] endCoords = externalMapService.getCoordinates(request.getDestinationName());

        // 3. 변환된 좌표로 경로 검색
        return externalMapService.searchRoutes(
                startCoords[0], startCoords[1],
                endCoords[0], endCoords[1]);
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
     * 검색 결과 상세 조회: 선택된 경로에 실시간 데이터와 통합 조언을 보강합니다.
     */
    public RouteEnrichedResponse enrichRouteDetails(RouteEnrichedResponse routeResponse) {
        enrichmentService.enrichRoute(routeResponse.getSteps());
        routeResponse.setIntegratedAdvice(adviceService.generateAdvice(routeResponse.getSteps()));
        return routeResponse;
    }
}
