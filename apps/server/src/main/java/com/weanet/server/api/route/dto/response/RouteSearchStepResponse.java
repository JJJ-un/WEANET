package com.weanet.server.dto;

import com.weanet.server.domain.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "경로 검색 목록용 구간 정보")
public class RouteSearchStepResponse {
    @Schema(description = "구간 순서", example = "1")
    private int sequence;

    @Schema(description = "교통수단 (WALK, BUS, SUBWAY)", example = "SUBWAY")
    private TransportType transportType;

    @Schema(description = "노선명", example = "2호선")
    private String lineName;

    @Schema(description = "노선 ID", example = "1002")
    private String lineId;

    @Schema(description = "구간 시작점", example = "강남역")
    private String startStationName;

    @Schema(description = "구간 시작점 ID (실시간 조회용)", example = "222")
    private String startStationId;

    @Schema(description = "구간 종료점", example = "양재역")
    private String endStationName;

    @Schema(description = "구간 종료점 ID (실시간 조회용)", example = "333")
    private String endStationId;

    @Schema(description = "위도", example = "37.1234")
    private double lat;

    @Schema(description = "경도", example = "127.1234")
    private double lng;

    @Schema(description = "소요 시간 (분)", example = "5")
    private int sectionTime;
}
