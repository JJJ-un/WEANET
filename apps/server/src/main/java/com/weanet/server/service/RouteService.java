package com.weanet.server.service;

import com.weanet.server.domain.Route;
import com.weanet.server.dto.RouteRequest;
import com.weanet.server.dto.RouteResponse;
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

    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        Route route = Route.builder()
                .name(request.getName())
                .departure(request.getDeparture())
                .destination(request.getDestination())
                .transportType(request.getTransportType())
                .routeNumber(request.getRouteNumber())
                .build();
        
        return RouteResponse.from(routeRepository.save(route));
    }

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(RouteResponse::from)
                .collect(Collectors.toList());
    }

    public RouteResponse getRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다. id=" + id));
        return RouteResponse.from(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}
