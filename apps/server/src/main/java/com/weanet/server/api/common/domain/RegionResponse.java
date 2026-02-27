package com.weanet.server.dto;

import com.weanet.server.domain.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "지역 응답 데이터")
public class RegionResponse {
    @Schema(description = "지역 ID", example = "1")
    private Long id;

    @Schema(description = "지역 명칭", example = "강남역")
    private String name;

    @Schema(description = "위도", example = "37.4979")
    private double lat;

    @Schema(description = "경도", example = "127.0276")
    private double lng;

    public static RegionResponse from(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .lat(region.getLat())
                .lng(region.getLng())
                .build();
    }
}
