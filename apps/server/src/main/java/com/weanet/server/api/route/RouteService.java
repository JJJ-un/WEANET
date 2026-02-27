package com.weanet.server.api.route;

import com.weanet.server.api.common.domain.Location;
import com.weanet.server.api.route.domain.Route;
import com.weanet.server.api.route.domain.RouteStep;
import com.weanet.server.api.route.dto.request.RouteSaveRequest;
import com.weanet.server.api.route.dto.response.*;
import com.weanet.server.dto.RouteSearchRequest;
import com.weanet.server.api.common.exception.BusinessException;
import com.weanet.server.api.common.exception.ErrorCode;
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

    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        double[] startCoords = externalMapService.getCoordinates(request.getDepartureName());
        double[] endCoords = externalMapService.getCoordinates(request.getDestinationName());

        return externalMapService.searchRoutes(
                startCoords[0], startCoords[1],
                endCoords[0], endCoords[1]);
    }

    @Transactional
    public RouteResponse createRoute(RouteSaveRequest request) {
        Route route = request.toEntity();
        Route savedRoute = routeRepository.save(route);
        return RouteResponse.from(savedRoute);
    }

    public RouteDetailResponse getRouteDetail(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_NOT_FOUND));

        List<RouteStepResponse> stepResponses = route.getSteps().stream()
                .map(step -> RouteStepResponse.builder()
                        .sequence(step.getSequence())
                        .transportType(step.getTransportType())
                        .lineName(step.getLineName())
                        .lineId(step.getLineId())
                        .startStationName(step.getStartLocation().getName())
                        .startStationId(step.getStartStationId())
                        .endStationName(step.getEndStationName())
                        .endStationId(step.getEndStationId())
                        .lat(step.getStartLocation().getLat())
                        .lng(step.getStartLocation().getLng())
                        .build())
                .collect(Collectors.toList());

        enrichmentService.enrichRoute(stepResponses);

        return RouteDetailResponse.builder()
                .name(route.getName())
                .departureName(route.getDepartureLocation().getName())
                .destinationName(route.getDestinationLocation().getName())
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

    public RouteEnrichedResponse enrichRouteDetails(RouteEnrichedResponse routeResponse) {
        enrichmentService.enrichRoute(routeResponse.getSteps());
        routeResponse.setIntegratedAdvice(adviceService.generateAdvice(routeResponse.getSteps()));
        return routeResponse;
    }
}
