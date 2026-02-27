package com.weanet.server.api.route.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weanet.server.api.route.domain.TransportType;
import com.weanet.server.api.weather.dto.WeatherResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상세 구간 정보 응답")
public class RouteEnrichedStepResponse {
    @Schema(description = "구간 순서", example = "1")
    private int sequence;

    @Schema(description = "교통수단 (WALK, BUS, SUBWAY)", example = "SUBWAY")
    private TransportType transportType;

    @Schema(description = "노선명", example = "2호선")
    private String lineName;

    @Schema(description = "노선 ID (실시간 조회용)", example = "1002")
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

    @Schema(description = "예상 도착 시각", example = "15:44")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime expectArrivalTime;

    @Schema(description = "경유역 명칭 리스트")
    private List<String> stations;

    @Schema(description = "실시간 날씨 정보")
    private WeatherResponse weather;

    @Schema(description = "실시간 혼잡도 정보", example = "보통")
    private String congestion;

    @Schema(description = "실시간 상태 알림 (지연, 장애 등)", example = "열차 지연: 2호선 상행선 신호 장애로 인해 10분 지연 운행 중")
    private String statusAlert;
}
