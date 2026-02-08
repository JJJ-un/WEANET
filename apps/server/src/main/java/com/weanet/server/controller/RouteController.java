package com.weanet.server.controller;

import com.weanet.server.dto.RouteRequest;
import com.weanet.server.dto.RouteResponse;
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

    @PostMapping
    @Operation(summary = "경로 생성", description = "사용자가 자주 다니는 경로를 저장합니다.")
    public RouteResponse createRoute(@RequestBody RouteRequest request) {
        return routeService.createRoute(request);
    }

    @GetMapping
    @Operation(summary = "전체 경로 조회", description = "저장된 모든 경로 목록을 조회합니다.")
    public List<RouteResponse> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "특정 경로 조회", description = "ID를 기반으로 특정 경로의 상세 정보를 조회합니다.")
    public RouteResponse getRoute(@PathVariable Long id) {
        return routeService.getRoute(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "경로 삭제", description = "ID를 기반으로 저장된 경로를 삭제합니다.")
    public void deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }
}
