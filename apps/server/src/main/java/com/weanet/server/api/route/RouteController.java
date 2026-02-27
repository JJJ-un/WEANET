package com.weanet.server.api.route;

import com.weanet.server.api.route.dto.request.RouteSaveRequest;
import com.weanet.server.api.route.dto.response.RouteEnrichedResponse;
import com.weanet.server.api.route.dto.response.RouteSearchResponse;
import com.weanet.server.api.route.dto.response.RouteSummaryResponse;
import com.weanet.server.api.route.dto.response.RouteResponse;
import com.weanet.server.api.route.dto.response.RouteDetailResponse;
import com.weanet.server.dto.RouteSearchRequest;
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

    @PostMapping("/search")
    @Operation(summary = "경로 검색", description = "출발지와 도착지를 기반으로 추천 경로를 검색합니다. (실시간 정보 제외)")
    public List<RouteSearchResponse> searchRoutes(@RequestBody RouteSearchRequest request) {
        return routeService.searchRoutes(request);
    }

    @PostMapping("/details")
    @Operation(summary = "검색 결과 상세 조회", description = "검색 결과 중 선택한 특정 경로에 대해 실시간 날씨, 혼잡도 정보를 보강하여 상세 정보를 제공합니다.")
    public RouteEnrichedResponse getRouteDetails(@RequestBody RouteEnrichedResponse request) {
        return routeService.enrichRouteDetails(request);
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
