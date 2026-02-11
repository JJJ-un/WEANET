package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "경로 검색 요청")
public class RouteSearchRequest {
    @Schema(description = "출발지 명칭", example = "강남역")
    private String departureName;

    @Schema(description = "출발지 위도", example = "37.4979")
    private double departureLat;

    @Schema(description = "출발지 경도", example = "127.0276")
    private double departureLng;

    @Schema(description = "도착지 명칭", example = "판교역")
    private String destinationName;

    @Schema(description = "도착지 위도", example = "37.3947")
    private double destinationLat;

    @Schema(description = "도착지 경도", example = "127.1112")
    private double destinationLng;
}
