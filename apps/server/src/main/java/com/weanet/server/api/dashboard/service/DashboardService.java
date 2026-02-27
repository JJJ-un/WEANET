package com.weanet.server.api.dashboard.service;

import com.weanet.server.api.route.domain.Route;
import com.weanet.server.api.report.dto.ReportResponse;
import com.weanet.server.api.dashboard.dto.RouteIntegratedReportResponse;
import com.weanet.server.api.route.dto.response.RouteStepResponse;
import com.weanet.server.api.common.exception.BusinessException;
import com.weanet.server.api.common.exception.ErrorCode;
import com.weanet.server.api.report.ReportRepository;
import com.weanet.server.api.route.RouteRepository;
import com.weanet.server.api.route.service.RouteEnrichmentService;
import com.weanet.server.api.route.service.RouteAdviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final RouteRepository routeRepository;
    private final ReportRepository reportRepository;
    private final RouteEnrichmentService enrichmentService;
    private final RouteAdviceService adviceService;

    /**
     * 특정 경로의 실시간 데이터를 종합하여 통합 리포트를 생성합니다.
     */
    public RouteIntegratedReportResponse getIntegratedReport(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_NOT_FOUND));

        // 1. 구간별 실시간 정보 보강을 위한 DTO 변환 (서비스 레이어에서 수행)
        List<RouteStepResponse> steps = route.getSteps().stream()
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

        // 2. 실시간 데이터 보강
        enrichmentService.enrichRoute(steps);

        // 3. 통합 조언 및 요약 정보 생성 (도메인 서비스 사용)
        String integratedAdvice = adviceService.generateAdvice(steps);
        List<RouteIntegratedReportResponse.StepSummary> stepSummaries = adviceService.getStepSummaries(steps);

        // 4. 해당 노선들에 대한 최신 제보 조회
        List<ReportResponse> recentReports = reportRepository.findByRouteIdOrderByCreatedAtDesc(routeId).stream()
                .distinct()
                .limit(5)
                .map(ReportResponse::from)
                .collect(Collectors.toList());

        return RouteIntegratedReportResponse.builder()
                .routeId(route.getId())
                .routeName(route.getName())
                .integratedAdvice(integratedAdvice)
                .stepSummaries(stepSummaries)
                .recentReports(recentReports)
                .build();
    }
}
