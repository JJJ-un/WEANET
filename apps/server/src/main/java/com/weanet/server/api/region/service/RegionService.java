package com.weanet.server.api.region.service;

import com.weanet.server.api.common.domain.Location;
import com.weanet.server.api.common.domain.Region;
import com.weanet.server.api.route.dto.response.PoiResponse;
import com.weanet.server.api.common.domain.RegionResponse;
import com.weanet.server.api.weather.dto.WeatherResponse;
import com.weanet.server.api.region.RegionRepository;
import com.weanet.server.api.common.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public List<RegionResponse> searchRegions(String keyword) {
        return null; // 실제 로직 복구 필요 시 추가
    }
}
