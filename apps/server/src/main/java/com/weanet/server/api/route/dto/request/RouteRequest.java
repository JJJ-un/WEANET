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
@Schema(description = "경로 생성 및 수정 요청 데이터")
public class RouteRequest {
    @Schema(description = "경로 별명 (예: 집-회사)", example = "출퇴근길")
    private String name;

    @Schema(description = "출발지", example = "강남역")
    private String departure;

    @Schema(description = "도착지", example = "판교역")
    private String destination;

    @Schema(description = "대중교통 타입 (BUS, SUBWAY)", example = "SUBWAY")
    private TransportType transportType;

    @Schema(description = "노선 번호 (예: 2호선, 143번 버스)", example = "신분당선")
    private String routeNumber;
}
