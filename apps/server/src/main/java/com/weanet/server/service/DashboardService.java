package com.weanet.server.service;

import com.weanet.server.domain.Route;
import com.weanet.server.dto.ReportResponse;
import com.weanet.server.dto.RouteIntegratedReportResponse;
import com.weanet.server.dto.RouteStepResponse;
import com.weanet.server.repository.ReportRepository;
import com.weanet.server.repository.RouteRepository;
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

    /**
     * 특정 경로의 실시간 데이터를 종합하여 통합 리포트를 생성합니다.
     */
    public RouteIntegratedReportResponse getIntegratedReport(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다."));

        // 1. 구간별 실시간 정보 보강을 위한 DTO 변환
        List<RouteStepResponse> steps = route.getSteps().stream()
                .map(step -> RouteStepResponse.builder()
                        .lineName(step.getLineName())
                        .transportType(step.getTransportType())
                        .lineId(step.getLineId())
                        .startStationId(step.getStartStationId())
                        .lat(step.getLat())
                        .lng(step.getLng())
                        .build())
                .collect(Collectors.toList());

        // 2. 실시간 데이터 보강
        enrichmentService.enrichRoute(steps);

        // 3. 통합 조언 생성 (엔티티 내 비즈니스 로직 사용)
        String integratedAdvice = route.generateAdvice(steps);

        // 4. 리포트용 요약 정보로 변환
        List<RouteIntegratedReportResponse.StepSummary> stepSummaries = steps.stream()
                .map(step -> RouteIntegratedReportResponse.StepSummary.builder()
                        .lineName(step.getLineName())
                        .status(step.getCongestion() != null ? step.getCongestion() : "정보 없음")
                        .weatherIcon(step.getWeather() != null ? step.getWeather().getWeather() : "Unknown")
                        .advice(step.getWeather() != null ? step.getWeather().getAdvice() : "")
                        .build())
                .collect(Collectors.toList());

        // 5. 해당 노선들에 대한 최신 제보 조회
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
