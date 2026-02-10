package com.weanet.server.controller;

import com.weanet.server.dto.*;
import com.weanet.server.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Route", description = "경로 관리 API")
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/search")
    @Operation(summary = "경로 검색", description = "출발지와 도착지를 기반으로 실시간 정보가 포함된 추천 경로를 검색합니다.")
    public List<RouteSearchResponse> searchRoutes(RouteSearchRequest request) {
        return routeService.searchRoutes(request);
    }

    @PostMapping
    @Operation(summary = "경로 저장", description = "선택한 경로를 대시보드 모니터링을 위해 저장합니다.")
    public RouteResponse createRoute(@RequestBody RouteSaveRequest request) {
        return routeService.createRoute(request);
    }

    @GetMapping
    @Operation(summary = "전체 경로 조회", description = "저장된 모든 경로 목록을 조회합니다.")
    public List<RouteResponse> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "저장된 경로 상세 조회", description = "ID를 기반으로 저장된 경로의 실시간 상세 상태를 조회합니다.")
    public RouteDetailResponse getRouteDetail(@PathVariable Long id) {
        return routeService.getRouteDetail(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "경로 삭제", description = "ID를 기반으로 저장된 경로를 삭제합니다.")
    public void deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }
}