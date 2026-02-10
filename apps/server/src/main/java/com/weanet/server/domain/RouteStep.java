package com.weanet.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    private int sequence; // 구간 순서 (1, 2, 3...)
    
    private String transportType; // 대중교통 타입 (WALK, BUS, SUBWAY)
    private String lineName; // 노선명 (2호선, 143번)
    private String lineId; // API 연동용 노선 고유 ID
    
    private String startStationName; // 구간 시작점 (역/정류장 이름)
    private String startStationId; // API 연동용 시작역 고유 ID
    
    private String endStationName; // 구간 종료점
    private String endStationId; // API 연동용 종료역 고유 ID

    private double lat; // 해당 구간의 대표 위도 (날씨 조회용)
    private double lng; // 해당 구간의 대표 경도 (날씨 조회용)

    @Builder
    public RouteStep(Route route, int sequence, String transportType, String lineName, String lineId, 
                     String startStationName, String startStationId, String endStationName, String endStationId,
                     double lat, double lng) {
        this.route = route;
        this.sequence = sequence;
        this.transportType = transportType;
        this.lineName = lineName;
        this.lineId = lineId;
        this.startStationName = startStationName;
        this.startStationId = startStationId;
        this.endStationName = endStationName;
        this.endStationId = endStationId;
        this.lat = lat;
        this.lng = lng;
    }
}
