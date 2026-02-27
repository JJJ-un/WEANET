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

    @Schema(description = "경로 요약", example = "2호선 -> 신분당선")
    private String summary;

    @Schema(description = "통합 조언 (상세 조회 시에만 제공)", example = "비가 오고 구간이 혼잡하니 평소보다 15분 일찍 출발하세요! ☔️🔴")
    private String integratedAdvice;

    @Schema(description = "상세 구간 리스트")
    private List<RouteSearchStepResponse> steps;
}
