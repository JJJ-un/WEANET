package com.weanet.server.controller;

import com.weanet.server.dto.PoiResponse;
import com.weanet.server.dto.RegionResponse;
import com.weanet.server.dto.WeatherResponse;
import com.weanet.server.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@Tag(name = "Region", description = "지역 저장 및 관리 API")
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/search")
    @Operation(summary = "지역 명칭으로 장소 후보 검색", description = "Tmap POI 검색을 이용해 해당 명칭과 관련된 장소 리스트(이름, 주소, 좌표)를 반환합니다.")
    public List<PoiResponse> searchLocation(@RequestParam String keyword) {
        return regionService.searchLocation(keyword);
    }

    @PostMapping
    @Operation(summary = "지역 저장", description = "명칭과 좌표를 입력받아 새로운 지역 엔티티로 저장합니다.")
    public RegionResponse saveRegion(
            @RequestParam String name,
            @RequestParam double lat,
            @RequestParam double lng) {
        return regionService.saveRegion(name, lat, lng);
    }

    @GetMapping
    @Operation(summary = "저장된 지역 목록 조회", description = "사용자가 지금까지 저장한 모든 지역 정보를 반환합니다.")
    public List<RegionResponse> getSavedRegions() {
        return regionService.getAllSavedRegions();
    }

    @GetMapping("/{id}/weather")
    @Operation(summary = "저장된 지역의 실시간 날씨 조회", description = "저장된 지역 ID를 기반으로 해당 장소의 실시간 날씨를 기상청 API에서 가져옵니다.")
    public WeatherResponse getRegionWeather(@PathVariable Long id) {
        return regionService.getRegionWeather(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "저장된 지역 삭제", description = "등록된 지역 정보를 삭제합니다.")
    public void deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
    }
}
