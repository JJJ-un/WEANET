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
    private String departureName;

    @Schema(description = "도착지", example = "판교역")
    private String destinationName;

    @Schema(description = "총 소요 시간", example = "45")
    private int totalTime;

    public static RouteResponse from(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .departureName(route.getDepartureName())
                .destinationName(route.getDestinationName())
                .totalTime(route.getTotalTime())
                .build();
    }
}
