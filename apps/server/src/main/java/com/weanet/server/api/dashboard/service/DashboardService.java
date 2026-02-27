package com.weanet.server.api.dashboard.service;

import com.weanet.server.api.route.domain.Route;
import com.weanet.server.api.report.dto.ReportResponse;
import com.weanet.server.api.dashboard.dto.RouteIntegratedReportResponse;
import com.weanet.server.api.route.dto.response.RouteStepResponse;
import com.weanet.server.api.common.exception.BusinessException;
import com.weanet.server.api.common.exception.ErrorCode;
import com.weanet.server.api.report.ReportRepository;
import com.weanet.server.api.route.service.RouteRepository;
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

    public RouteIntegratedReportResponse getIntegratedReport(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_NOT_FOUND));

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

        enrichmentService.enrichRoute(steps);

        String integratedAdvice = adviceService.generateAdvice(steps);
        List<RouteIntegratedReportResponse.StepSummary> stepSummaries = adviceService.getStepSummaries(steps);

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
