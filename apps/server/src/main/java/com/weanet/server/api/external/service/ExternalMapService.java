package com.weanet.server.api.external.service;

import com.weanet.server.api.external.dto.PoiResponse;
import com.weanet.server.api.route.domain.TransportType;
import com.weanet.server.api.route.dto.RouteSearchResponse;
import com.weanet.server.api.route.dto.RouteSearchStepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalMapService {

    private final RestTemplate restTemplate;

    @Value("${tmap.api.key}")
    private String apiKey;

    @Value("${tmap.api.url}")
    private String apiUrl;

    @Value("${tmap.api.poi.url}")
    private String poiApiUrl;

    /**
     * 키워드로 장소 리스트를 검색합니다 (POI 검색 API).
     */
    public List<PoiResponse> searchPoi(String keyword) {
        List<PoiResponse> results = new ArrayList<>();
        try {
            String url = UriComponentsBuilder.fromUriString(poiApiUrl)
                    .queryParam("version", 1)
                    .queryParam("searchKeyword", keyword)
                    .queryParam("count", 10) // 상위 10개 검색
                    .queryParam("appKey", apiKey)
                    .build().toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("searchPoiInfo")) {
                Map<String, Object> searchPoiInfo = (Map<String, Object>) response.get("searchPoiInfo");
                Map<String, Object> pois = (Map<String, Object>) searchPoiInfo.get("pois");
                List<Map<String, Object>> poiList = (List<Map<String, Object>>) pois.get("poi");

                if (poiList != null) {
                    for (Map<String, Object> poi : poiList) {
                        results.add(PoiResponse.builder()
                                .name((String) poi.get("name"))
                                .address(buildAddress(poi))
                                .lat(Double.parseDouble((String) poi.get("frontLat")))
                                .lng(Double.parseDouble((String) poi.get("frontLon")))
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("POI 검색 중 오류 발생 (키워드: {}): {}", keyword, e.getMessage());
        }
        return results;
    }

    private String buildAddress(Map<String, Object> poi) {
        String upperAddr = (String) poi.get("upperAddrName");
        String middleAddr = (String) poi.get("middleAddrName");
        String lowerAddr = (String) poi.get("lowerAddrName");
        String detailAddr = (String) poi.get("detailAddrName");
        
        return String.format("%s %s %s %s", 
                upperAddr != null ? upperAddr : "", 
                middleAddr != null ? middleAddr : "", 
                lowerAddr != null ? lowerAddr : "", 
                detailAddr != null ? detailAddr : "").trim();
    }

    /**
     * 장소 명칭(keyword)을 기반으로 첫 번째 좌표(위도, 경도)를 조회합니다.
     */
    public double[] getCoordinates(String keyword) {
        List<PoiResponse> results = searchPoi(keyword);
        if (!results.isEmpty()) {
            PoiResponse first = results.get(0);
            return new double[]{first.getLat(), first.getLng()};
        }
        return new double[]{37.5665, 126.9780}; // 서울시청 기본 좌표
    }

    /**
     * Tmap 대중교통 경로 검색 API를 호출합니다.
     */
    public List<RouteSearchResponse> searchRoutes(double startLat, double startLng, double endLat, double endLng) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", apiKey);
            headers.set("accept", "application/json");
            headers.set("content-type", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "startX", String.valueOf(startLng),
                    "startY", String.valueOf(startLat),
                    "endX", String.valueOf(endLng),
                    "endY", String.valueOf(endLat),
                    "lang", 0,
                    "format", "json",
                    "count", 5
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            
            return parseTmapResponse(response);

        } catch (Exception e) {
            log.error("Error calling Tmap API: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<RouteSearchResponse> parseTmapResponse(Map<String, Object> response) {
        List<RouteSearchResponse> results = new ArrayList<>();
        
        try {
            if (response == null || !response.containsKey("metaData")) return results;
            
            Map<String, Object> metaData = (Map<String, Object>) response.get("metaData");
            Map<String, Object> plan = (Map<String, Object>) metaData.get("plan");
            if (plan == null || !plan.containsKey("itineraries")) return results;
            
            List<Map<String, Object>> itineraries = (List<Map<String, Object>>) plan.get("itineraries");

            for (Map<String, Object> itinerary : itineraries) {
                int totalTime = ((Number) itinerary.get("totalTime")).intValue() / 60;
                
                int totalFare = 0;
                Object fareObj = itinerary.get("fare");
                if (fareObj instanceof Map<?, ?> fareMap) {
                    Object regularObj = fareMap.get("regular");
                    if (regularObj instanceof Map<?, ?> regularMap) {
                        totalFare = ((Number) regularMap.get("totalFare")).intValue();
                    }
                }

                int transferCount = ((Number) itinerary.get("transferCount")).intValue();
                
                List<Map<String, Object>> legs = (List<Map<String, Object>>) itinerary.get("legs");
                List<RouteSearchStepResponse> steps = new ArrayList<>();
                StringBuilder summary = new StringBuilder();

                int seq = 1;
                for (Map<String, Object> leg : legs) {
                    String mode = (String) leg.get("mode");
                    TransportType transportType = convertToTransportType(mode);
                    String lineName = (String) leg.get("route");
                    String lineId = (String) leg.get("routeId");
                    
                    if (lineName != null && transportType != TransportType.WALK) {
                        if (summary.length() > 0) summary.append(" -> ");
                        summary.append(lineName);
                    }

                    Map<String, Object> start = (Map<String, Object>) leg.get("start");
                    Map<String, Object> end = (Map<String, Object>) leg.get("end");

                    String startStationId = null;
                    String endStationId = null;
                    
                    if (leg.containsKey("passStopList")) {
                        Map<String, Object> passStopList = (Map<String, Object>) leg.get("passStopList");
                        if (passStopList != null && passStopList.containsKey("stations")) {
                            List<Map<String, Object>> stations = (List<Map<String, Object>>) passStopList.get("stations");
                            if (stations != null && !stations.isEmpty()) {
                                Object startIdObj = stations.get(0).get("stationID");
                                Object endIdObj = stations.get(stations.size() - 1).get("stationID");
                                startStationId = startIdObj != null ? String.valueOf(startIdObj) : null;
                                endStationId = endIdObj != null ? String.valueOf(endIdObj) : null;
                            }
                        }
                    }

                    steps.add(RouteSearchStepResponse.builder()
                            .sequence(seq++)
                            .transportType(transportType)
                            .lineName(lineName != null ? lineName : (transportType == TransportType.WALK ? "도보" : "정보 없음"))
                            .lineId(lineId)
                            .startStationName(start != null ? (String) start.get("name") : "출발지")
                            .startStationId(startStationId)
                            .endStationName(end != null ? (String) end.get("name") : "도착지")
                            .endStationId(endStationId)
                            .sectionTime(((Number) leg.get("sectionTime")).intValue() / 60)
                            .lat(start != null ? ((Number) start.get("lat")).doubleValue() : 0.0)
                            .lng(start != null ? ((Number) start.get("lon")).doubleValue() : 0.0)
                            .build());
                }

                results.add(RouteSearchResponse.builder()
                        .totalTime(totalTime)
                        .totalFare(totalFare)
                        .transferCount(transferCount)
                        .summary(summary.toString())
                        .steps(steps)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error parsing Tmap response: {}", e.getMessage());
        }

        return results;
    }

    private TransportType convertToTransportType(String mode) {
        if (mode == null) return TransportType.WALK;
        try {
            return TransportType.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TransportType.WALK;
        }
    }
}
