package com.weanet.server.api.dashboard;

import com.weanet.server.api.dashboard.dto.RouteIntegratedReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "대시보드 통합 관리 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{routeId}")
    @Operation(summary = "통합 리포트 조회", description = "저장된 경로의 날씨, 혼잡도, 제보를 종합하여 최적의 이동 조언을 제공합니다.")
    public RouteIntegratedReportResponse getIntegratedReport(@PathVariable Long routeId) {
        return dashboardService.getIntegratedReport(routeId);
    }
}
