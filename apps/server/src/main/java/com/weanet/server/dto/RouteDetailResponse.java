package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "경로 상세 정보 응답")
public class RouteDetailResponse {
    @Schema(description = "경로 별칭", example = "출근길")
    private String name;

    @Schema(description = "출발지", example = "강남역")
    private String departureName;

    @Schema(description = "도착지", example = "판교역")
    private String destinationName;

    @Schema(description = "총 소요 시간", example = "30")
    private int totalTime;

    @Schema(description = "총 요금", example = "1250")
    private int totalFare;

    @Schema(description = "상세 구간 리스트")
    private List<RouteStepResponse> steps;
}
