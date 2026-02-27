package com.weanet.server.api.external.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Tmap POI 검색 결과")
public class PoiResponse {
    @Schema(description = "장소 명칭", example = "강남역 2호선")
    private String name;

    @Schema(description = "전체 주소", example = "서울 강남구 강남대로 지하 396")
    private String address;

    @Schema(description = "위도", example = "37.4979")
    private double lat;

    @Schema(description = "경도", example = "127.0276")
    private double lng;
}
