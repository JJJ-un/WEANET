package com.weanet.server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 경로 별명 (예: 집-회사)
    private String departure; // 출발지
    private String destination; // 도착지
    private String transportType; // 대중교통 타입 (BUS, SUBWAY)
    private String routeNumber; // 노선 번호 (예: 2호선, 143번 버스)

    @Builder
    public Route(String name, String departure, String destination, String transportType, String routeNumber) {
        this.name = name;
        this.departure = departure;
        this.destination = destination;
        this.transportType = transportType;
        this.routeNumber = routeNumber;
    }
}
