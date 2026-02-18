package com.weanet.server.dto;

import com.weanet.server.domain.Route;
import com.weanet.server.domain.RouteStep;
import com.weanet.server.domain.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteStepSaveRequest {
    private int sequence;
    private TransportType transportType;
    private String lineName;
    private String lineId;
    private String startStationName;
    private String startStationId;
    private String endStationName;
    private String endStationId;
    private double lat;
    private double lng;

    public RouteStep toEntity(Route route) {
        return RouteStep.builder()
                .route(route)
                .sequence(this.sequence)
                .transportType(this.transportType)
                .lineName(this.lineName)
                .lineId(this.lineId)
                .startStationName(this.startStationName)
                .startStationId(this.startStationId)
                .endStationName(this.endStationName)
                .endStationId(this.endStationId)
                .lat(this.lat)
                .lng(this.lng)
                .build();
    }
}
