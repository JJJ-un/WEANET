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
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 경로 별명 (예: 집-회사)
    
    private String departureName; // 출발지 명칭
    private double departureLat; // 출발지 위도
    private double departureLng; // 출발지 경도
    
    private String destinationName; // 도착지 명칭
    private double destinationLat; // 도착지 위도
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
    }
}