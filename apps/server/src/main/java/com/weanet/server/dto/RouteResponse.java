package com.weanet.server.dto;

import com.weanet.server.domain.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "경로 응답 데이터")
public class RouteResponse {
    @Schema(description = "경로 ID", example = "1")
    private Long id;

    @Schema(description = "경로 별명", example = "출퇴근길")
    private String name;

    @Schema(description = "출발지", example = "강남역")
    private String departure;

    @Schema(description = "도착지", example = "판교역")
    private String destination;

    @Schema(description = "대중교통 타입", example = "SUBWAY")
    private String transportType;

    @Schema(description = "노선 번호", example = "신분당선")
    private String routeNumber;

    public static RouteResponse from(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .departure(route.getDeparture())
                .destination(route.getDestination())
                .transportType(route.getTransportType())
                .routeNumber(route.getRouteNumber())
                .build();
    }
}
