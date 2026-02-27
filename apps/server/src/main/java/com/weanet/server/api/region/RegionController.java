package com.weanet.server.api.region;

import com.weanet.server.api.region.service.RegionService;
import com.weanet.server.api.common.domain.RegionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@Tag(name = "Region", description = "지역 정보 관리 API")
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/search")
    @Operation(summary = "지역 검색", description = "키워드를 기반으로 행정구역(시도, 시군구, 읍면동)을 검색합니다.")
    public List<RegionResponse> searchRegions(@RequestParam String keyword) {
        return regionService.searchRegions(keyword);
    }
}
