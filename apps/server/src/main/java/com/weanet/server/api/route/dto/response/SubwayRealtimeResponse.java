package com.weanet.server.api.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지하철 실시간 도착 정보 응답")
public class SubwayRealtimeResponse {
    @Schema(description = "노선 명칭", example = "2호선")
    private String lineName;

    @Schema(description = "도착 안내 메시지", example = "3분 45초 후 도착")
    private String arrivalMessage;

    @Schema(description = "지연 여부", example = "false")
    private boolean isDelayed;
    
    @Schema(description = "열차 상태 (정상, 지연 등)", example = "정상")
    private String status;
}
