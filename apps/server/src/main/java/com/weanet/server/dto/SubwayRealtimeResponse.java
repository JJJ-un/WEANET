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
@Schema(description = "지하철 실시간 정보 응답")
public class SubwayRealtimeResponse {
    @Schema(description = "노선명", example = "2호선")
    private String lineName;

    @Schema(description = "도착지 방면", example = "성수행 (외선순환)")
    private String destination;

    @Schema(description = "실시간 도착 메시지", example = "3분 45초 후 도착")
    private String arrivalMessage;

    @Schema(description = "도착 코드 (0:진입, 1:도착, 99:운행중 등)", example = "1")
    private String arrivalCode;

    @Schema(description = "지연 여부", example = "false")
    private boolean isDelayed;
}
