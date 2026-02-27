package com.weanet.server.domain;

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
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name; // 경로 별명 (예: 집-회사)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_region_id", nullable = false)
    private Region departureRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_region_id", nullable = false)
    private Region destinationRegion;

    private int totalTime; // 총 소요 시간 (분)
    private int totalFare; // 총 요금
    private int transferCount; // 환승 횟수

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteStep> steps = new ArrayList<>();

    @Builder
    public Route(String name, Region departureRegion, Region destinationRegion, 
                 int totalTime, int totalFare, int transferCount) {
        validate(name, departureRegion, destinationRegion, totalTime, totalFare, transferCount);
        this.name = name;
        this.departureRegion = departureRegion;
        this.destinationRegion = destinationRegion;
        this.totalTime = totalTime;
        this.totalFare = totalFare;
        this.transferCount = transferCount;
    }

    private void validate(String name, Region departureRegion, Region destinationRegion, 
                          int totalTime, int totalFare, int transferCount) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("경로 이름은 필수입니다.");
        if (departureRegion == null) throw new IllegalArgumentException("출발지 지역 정보는 필수입니다.");
        if (destinationRegion == null) throw new IllegalArgumentException("도착지 지역 정보는 필수입니다.");
        
        if (totalTime < 0) throw new IllegalArgumentException("총 소요 시간은 음수일 수 없습니다.");
        if (totalFare < 0) throw new IllegalArgumentException("총 요금은 음수일 수 없습니다.");
        if (transferCount < 0) throw new IllegalArgumentException("환승 횟수는 음수일 수 없습니다.");
    }

    public void addStep(RouteStep step) {
        this.steps.add(step);
        if (step.getRoute() != this) {
            step.assignRoute(this);
        }
    }
}