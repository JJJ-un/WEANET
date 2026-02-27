package com.weanet.server.api.route.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "경로 검색 결과 응답")
public class RouteSearchResponse {
    @Schema(description = "총 소요 시간 (분)", example = "30")
    private int totalTime;

    @Schema(description = "총 요금 (원)", example = "1250")
    private int totalFare;

    @Schema(description = "환승 횟수", example = "1")
    private int transferCount;

    @Schema(description = "예상 출발 시각", example = "2026-02-27T08:30:00")
    private java.time.LocalDateTime expectDepartureTime;

    @Schema(description = "예상 도착 시각", example = "2026-02-27T09:15:00")
    private java.time.LocalDateTime expectArrivalTime;

    @Schema(description = "경로 요약", example = "2호선 -> 신분당선")
    private String summary;

    @Schema(description = "상세 구간 리스트")
    private List<RouteSearchStepResponse> steps;
}
