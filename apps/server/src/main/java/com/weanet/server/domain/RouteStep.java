package com.weanet.server.domain;

import com.weanet.server.dto.RouteStepResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RouteStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @lombok.EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private int sequence; // 구간 순서 (1, 2, 3...)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType; // 대중교통 타입 (WALK, BUS, SUBWAY)

    private String lineName; // 노선명 (2호선, 143번)
    private String lineId; // API 연동용 노선 고유 ID
    
    @Column(nullable = false)
    private String startStationName; // 구간 시작점 (역/정류장 이름)
    private String startStationId; // API 연동용 시작역 고유 ID
    
    @Column(nullable = false)
    private String endStationName; // 구간 종료점
    private String endStationId; // API 연동용 종료역 고유 ID

    @Column(nullable = false)
    private double lat; // 해당 구간의 대표 위도 (날씨 조회용)
    @Column(nullable = false)
    private double lng; // 해당 구간의 대표 경도 (날씨 조회용)

    @Builder
    public RouteStep(Route route, int sequence, TransportType transportType, String lineName, String lineId, 
                     String startStationName, String startStationId, String endStationName, String endStationId,
                     double lat, double lng) {
        validate(sequence, transportType, startStationName, endStationName, lat, lng);
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

    private void validate(int sequence, TransportType transportType, String startStationName, String endStationName, 
                          double lat, double lng) {
        if (sequence <= 0) throw new IllegalArgumentException("구간 순서는 0보다 커야 합니다.");
        if (transportType == null) throw new IllegalArgumentException("교통수단 타입은 필수입니다.");
        if (startStationName == null || startStationName.isBlank()) throw new IllegalArgumentException("구간 시작점 이름은 필수입니다.");
        if (endStationName == null || endStationName.isBlank()) throw new IllegalArgumentException("구간 종료점 이름은 필수입니다.");
        
        validateCoordinates(lat, lng);
    }

    private void validateCoordinates(double lat, double lng) {
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("위도 범위가 올바르지 않습니다 (-90 ~ 90).");
        if (lng < -180 || lng > 180) throw new IllegalArgumentException("경도 범위가 올바르지 않습니다 (-180 ~ 180).");
    }

    public void assignRoute(Route route) {
        this.route = route;
    }
}
