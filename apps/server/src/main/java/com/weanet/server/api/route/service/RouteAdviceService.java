package com.weanet.server.api.route.service;

import com.weanet.server.api.dashboard.dto.response.RouteIntegratedReportResponse;
import com.weanet.server.api.route.dto.response.RouteEnrichedStepResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 보강된 데이터를 바탕으로 비즈니스 판단(조언, 요약)을 내리는 도메인 서비스
 */
@Service
public class RouteAdviceService {

    public String generateAdvice(List<RouteEnrichedStepResponse> enrichedSteps) {
        if (enrichedSteps == null || enrichedSteps.isEmpty()) {
            return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";
        }

        if (hasDelay(enrichedSteps)) return "현재 이용하실 지하철 노선에 공식 지연/장애 공지가 있습니다. 상세 정보를 확인해 주세요! ⚠️🚇";
        
        boolean isRainy = hasRainySection(enrichedSteps);
        boolean isCongested = hasCongestion(enrichedSteps);

        if (isRainy && isCongested) return "현재 경로에 비가 오고 대중교통이 매우 혼잡합니다. 평소보다 15분 일찍 출발하시고 우산을 꼭 챙기세요! ☔️🔴";
        if (isRainy) return "경로 구간에 비 소식이 있습니다. 이동 시 우산을 챙기시고 발밑 조심하세요! ☔️";
        if (isCongested) return "현재 이용하실 노선이 많이 혼잡합니다. 여유가 있다면 다음 열차/버스를 이용해 보세요. 🔴";
        
        return "현재 경로의 상태가 대체로 양호합니다. 즐거운 이동 되세요! 😊";
    }

    public List<RouteIntegratedReportResponse.StepSummary> getStepSummaries(List<RouteEnrichedStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .map(step -> RouteIntegratedReportResponse.StepSummary.builder()
                        .lineName(step.getLineName())
                        .status(step.getCongestion() != null ? step.getCongestion() : "정보 없음")
                        .weatherIcon(step.getWeather() != null ? step.getWeather().getWeather() : "Unknown")
                        .advice(step.getWeather() != null ? step.getWeather().getAdvice() : "")
                        .build())
                .collect(Collectors.toList());
    }

    private boolean hasCongestion(List<RouteEnrichedStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getCongestion() != null)
                .anyMatch(s -> "혼잡".equals(s.getCongestion()));
    }

    private boolean hasRainySection(List<RouteEnrichedStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getWeather() != null && s.getWeather().getAdvice() != null)
                .anyMatch(s -> s.getWeather().getAdvice().contains("비"));
    }

    private boolean hasDelay(List<RouteEnrichedStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getStatusAlert() != null)
                .anyMatch(s -> s.getStatusAlert().contains("지연") || 
                              s.getStatusAlert().contains("장애") || 
                              s.getStatusAlert().contains("점검"));
    }
}
