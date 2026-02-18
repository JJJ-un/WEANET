package com.weanet.server.domain;

import com.weanet.server.dto.RouteStepResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 경로 별명 (예: 집-회사)
    
    @Column(nullable = false)
    private String departureName; // 출발지 명칭
    @Column(nullable = false)
    private double departureLat; // 출발지 위도
    @Column(nullable = false)
    private double departureLng; // 출발지 경도
    
    @Column(nullable = false)
    private String destinationName; // 도착지 명칭
    @Column(nullable = false)
    private double destinationLat; // 도착지 위도
    @Column(nullable = false)
    private double destinationLng; // 도착지 경도

    private int totalTime; // 총 소요 시간 (분)
    private int totalFare; // 총 요금
    private int transferCount; // 환승 횟수

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteStep> steps = new ArrayList<>();

    @Builder
    public Route(String name, String departureName, double departureLat, double departureLng, 
                 String destinationName, double destinationLat, double destinationLng, 
                 int totalTime, int totalFare, int transferCount) {
        this.name = name;
        this.departureName = departureName;
        this.departureLat = departureLat;
        this.departureLng = departureLng;
        this.destinationName = destinationName;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.totalTime = totalTime;
        this.totalFare = totalFare;
        this.transferCount = transferCount;
    }

    public void addStep(RouteStep step) {
        this.steps.add(step);
        if (step.getRoute() != this) {
            step.assignRoute(this);
        }
    }

    /**
     * 경로 내에 혼잡한 구간이 있는지 확인합니다.
     */
    public boolean hasCongestion(List<RouteStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getCongestion() != null)
                .anyMatch(s -> "혼잡".equals(s.getCongestion()));
    }

    /**
     * 경로 내에 비 소식이 있는 구간이 있는지 확인합니다.
     */
    public boolean hasRainySection(List<RouteStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getWeather() != null && s.getWeather().getAdvice() != null)
                .anyMatch(s -> s.getWeather().getAdvice().contains("비"));
    }

    /**
     * 경로 내에 지연/장애 공지가 있는 구간이 있는지 확인합니다.
     */
    public boolean hasDelay(List<RouteStepResponse> enrichedSteps) {
        return enrichedSteps.stream()
                .filter(s -> s.getArrivalMessage() != null)
                .anyMatch(s -> s.getArrivalMessage().contains("지연") || 
                              s.getArrivalMessage().contains("장애") || 
                              s.getArrivalMessage().contains("점검"));
    }

    /**
     * 보강된 실시간 데이터를 바탕으로 통합 조언 메시지를 생성합니다.
     */
    public String generateAdvice(List<RouteStepResponse> enrichedSteps) {
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
}