package com.weanet.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteStepSaveRequest {
    private int sequence;
    private String transportType;
    private String lineName;
    private String lineId;
    private String startStationName;
    private String startStationId;
    private String endStationName;
    private String endStationId;
    private double lat;
    private double lng;
}
