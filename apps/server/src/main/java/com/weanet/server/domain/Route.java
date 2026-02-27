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
        validate(name, departureName, departureLat, departureLng, destinationName, destinationLat, destinationLng, totalTime, totalFare, transferCount);
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

    private void validate(String name, String departureName, double departureLat, double departureLng, 
                          String destinationName, double destinationLat, double destinationLng, 
                          int totalTime, int totalFare, int transferCount) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("경로 이름은 필수입니다.");
        if (departureName == null || departureName.isBlank()) throw new IllegalArgumentException("출발지 이름은 필수입니다.");
        if (destinationName == null || destinationName.isBlank()) throw new IllegalArgumentException("도착지 이름은 필수입니다.");
        
        validateCoordinates(departureLat, departureLng);
        validateCoordinates(destinationLat, destinationLng);
        
        if (totalTime < 0) throw new IllegalArgumentException("총 소요 시간은 음수일 수 없습니다.");
        if (totalFare < 0) throw new IllegalArgumentException("총 요금은 음수일 수 없습니다.");
        if (transferCount < 0) throw new IllegalArgumentException("환승 횟수는 음수일 수 없습니다.");
    }

    private void validateCoordinates(double lat, double lng) {
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("위도 범위가 올바르지 않습니다 (-90 ~ 90).");
        if (lng < -180 || lng > 180) throw new IllegalArgumentException("경도 범위가 올바르지 않습니다 (-180 ~ 180).");
    }

    public void addStep(RouteStep step) {
        this.steps.add(step);
        if (step.getRoute() != this) {
            step.assignRoute(this);
        }
    }
}