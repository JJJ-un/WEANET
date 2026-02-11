package com.weanet.server.service;

import com.weanet.server.domain.Route;
import com.weanet.server.domain.RouteStep;
import com.weanet.server.dto.ReportResponse;
import com.weanet.server.dto.RouteIntegratedReportResponse;
import com.weanet.server.dto.WeatherResponse;
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
    private final WeatherService weatherService;
    private final CongestionService congestionService;

    /**
     * 특정 경로의 실시간 데이터를 종합하여 통합 리포트를 생성합니다.
     */
    public RouteIntegratedReportResponse getIntegratedReport(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다."));

        // 1. 구간별 요약 정보 및 실시간 데이터 조회
        List<RouteIntegratedReportResponse.StepSummary> stepSummaries = route.getSteps().stream()
                .map(this::createStepSummary)
                .collect(Collectors.toList());

        // 2. 해당 노선들에 대한 최신 제보 조회 (각 구간의 노선 제보 합산)
        List<ReportResponse> recentReports = route.getSteps().stream()
                .flatMap(step -> reportRepository.findByRouteIdOrderByCreatedAtDesc(routeId).stream()) // 실제로는 노선별 조회가 적절하나 우선 경로제보 활용
                .distinct()
                .limit(5)
                .map(ReportResponse::from)
                .collect(Collectors.toList());

        // 3. 지능형 통합 조언 생성
        String integratedAdvice = generateIntegratedAdvice(stepSummaries);

        return RouteIntegratedReportResponse.builder()
                .routeId(route.getId())
                .routeName(route.getName())
                .integratedAdvice(integratedAdvice)
                .stepSummaries(stepSummaries)
                .recentReports(recentReports)
                .build();
    }

    private RouteIntegratedReportResponse.StepSummary createStepSummary(RouteStep step) {
        WeatherResponse weather = weatherService.getWeatherByCoordinates(step.getLat(), step.getLng());
        String congestion = "보통";
        
        if ("SUBWAY".equals(step.getTransportType()) || "BUS".equals(step.getTransportType())) {
            congestion = congestionService.getCongestion(step.getTransportType(), step.getLineId(), step.getStartStationId());
        }

        return RouteIntegratedReportResponse.StepSummary.builder()
                .lineName(step.getLineName())
                .status(congestion)
                .weatherIcon(weather.getWeather())
                .advice(weather.getAdvice())
                .build();
    }

    /**
     * 날씨와 혼잡도를 결합하여 사용자에게 줄 최적의 조언을 생성합니다.
     */
    private String generateIntegratedAdvice(List<RouteIntegratedReportResponse.StepSummary> summaries) {
        boolean isRainy = summaries.stream().anyMatch(s -> s.getAdvice().contains("비"));
        boolean isCongested = summaries.stream().anyMatch(s -> s.getStatus().equals("혼잡"));

        if (isRainy && isCongested) {
            return "현재 경로에 비가 오고 대중교통이 매우 혼잡합니다. 평소보다 15분 일찍 출발하시고 우산을 꼭 챙기세요! ☔️🔴";
        } else if (isRainy) {
            return "경로 구간에 비 소식이 있습니다. 이동 시 우산을 챙기시고 발밑 조심하세요! ☔️";
        } else if (isCongested) {
            return "현재 이용하실 노선이 많이 혼잡합니다. 여유가 있다면 다음 열차/버스를 이용해 보세요. 🔴";
        }
        
        return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";
    }
}
