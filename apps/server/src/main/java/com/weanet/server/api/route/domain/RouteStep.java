package com.weanet.server.api.route.domain;

import com.weanet.server.global.common.BaseEntity;
import com.weanet.server.global.common.Location;
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
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "start_station_name", nullable = false)),
        @AttributeOverride(name = "lat", column = @Column(name = "lat", nullable = false)),
        @AttributeOverride(name = "lng", column = @Column(name = "lng", nullable = false))
    })
    private Location startLocation; // 시작 지점 정보 (명칭 + 좌표)

    private String startStationId; // API 연동용 시작역 고유 ID
    
    @Column(nullable = false)
    private String endStationName; // 구간 종료점
    private String endStationId; // API 연동용 종료역 고유 ID

    @Builder
    public RouteStep(Route route, int sequence, TransportType transportType, String lineName, String lineId, 
                     Location startLocation, String startStationId, String endStationName, String endStationId) {
        validate(sequence, transportType, startLocation, endStationName);
        this.route = route;
        this.sequence = sequence;
        this.transportType = transportType;
        this.lineName = lineName;
        this.lineId = lineId;
        this.startLocation = startLocation;
        this.startStationId = startStationId;
        this.endStationName = endStationName;
        this.endStationId = endStationId;
    }

    private void validate(int sequence, TransportType transportType, Location startLocation, String endStationName) {
        if (sequence <= 0) throw new IllegalArgumentException("구간 순서는 0보다 커야 합니다.");
        if (transportType == null) throw new IllegalArgumentException("교통수단 타입은 필수입니다.");
        if (startLocation == null) throw new IllegalArgumentException("구간 시작 지점 정보는 필수입니다.");
        if (endStationName == null || endStationName.isBlank()) throw new IllegalArgumentException("구간 종료점 이름은 필수입니다.");
    }

    public void assignRoute(Route route) {
        this.route = route;
    }
}
