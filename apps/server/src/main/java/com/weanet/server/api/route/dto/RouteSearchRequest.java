package com.weanet.server.api.route.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "경로 검색 요청 (명칭 기반)")
public class RouteSearchRequest {
    @Schema(description = "출발지 명칭", example = "서울역")
    private String departureName;

    @Schema(description = "도착지 명칭", example = "강남역")
    private String destinationName;
}
