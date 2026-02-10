package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상세 구간 정보 응답")
public class RouteStepResponse {
    @Schema(description = "구간 순서", example = "1")
    private int sequence;

    @Schema(description = "교통수단 (WALK, BUS, SUBWAY)", example = "SUBWAY")
    private String transportType;

    @Schema(description = "노선명", example = "2호선")
    private String lineName;

    @Schema(description = "구간 시작점", example = "강남역")
    private String startStationName;

    @Schema(description = "구간 종료점", example = "양재역")
    private String endStationName;

    @Schema(description = "소요 시간 (분)", example = "5")
    private int sectionTime;

    @Schema(description = "실시간 날씨 정보")
    private WeatherResponse weather;

    @Schema(description = "실시간 혼잡도 정보", example = "보통")
    private String congestion;
}
