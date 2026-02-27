package com.weanet.server.api.region.service;

import com.weanet.server.api.external.dto.PoiResponse;
import com.weanet.server.api.external.service.ExternalMapService;
import com.weanet.server.api.external.util.KmaCoordinateConverter;
import com.weanet.server.api.region.domain.Region;
import com.weanet.server.api.region.dto.RegionResponse;
import com.weanet.server.api.region.repository.RegionRepository;
import com.weanet.server.api.weather.dto.WeatherResponse;
import com.weanet.server.api.weather.service.WeatherService;
import com.weanet.server.global.common.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private final ExternalMapService externalMapService;
    private final WeatherService weatherService;
    private final KmaCoordinateConverter coordinateConverter;

    /**
     * 키워드로 지역 후보들을 검색합니다.
     */
    public List<PoiResponse> searchLocation(String keyword) {
        return externalMapService.searchPoi(keyword);
    }

    /**
     * 새로운 지역을 저장하거나 기존 정보를 업데이트합니다.
     */
    @Transactional
    public RegionResponse saveRegion(String name, double lat, double lng) {
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(lat, lng);
        Location location = Location.builder().name(name).lat(lat).lng(lng).build();
        
        Region region = regionRepository.findByLocationName(name)
                .map(existingRegion -> {
                    existingRegion.updateLocation(location, grid.nx, grid.ny);
                    return existingRegion;
                })
                .orElseGet(() -> Region.builder()
                        .location(location)
                        .nx(grid.nx)
                        .ny(grid.ny)
                        .build());
        
        Region savedRegion = regionRepository.save(region);
        return RegionResponse.from(savedRegion);
    }

    /**
     * 저장된 모든 지역 정보를 조회합니다.
     */
    public List<RegionResponse> getAllSavedRegions() {
        return regionRepository.findAll().stream()
                .map(RegionResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 지역의 실시간 날씨 정보를 조회합니다.
     */
    public WeatherResponse getRegionWeather(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 지역 정보를 찾을 수 없습니다."));
        
        return weatherService.getWeatherByCoordinates(region.getLat(), region.getLng());
    }

    @Transactional
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
