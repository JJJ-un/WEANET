package com.weanet.server.service;

import com.weanet.server.domain.TransportType;
import com.weanet.server.dto.RouteSearchResponse;
import com.weanet.server.dto.RouteSearchStepResponse;
import com.weanet.server.dto.RouteStepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * 장소 명칭(keyword)을 기반으로 좌표(위도, 경도)를 조회합니다.
     */
    public double[] getCoordinates(String keyword) {
        try {
            String url = UriComponentsBuilder.fromUriString(poiApiUrl)
                    .queryParam("version", 1)
                    .queryParam("searchKeyword", keyword)
                    .queryParam("count", 1)
                    .queryParam("appKey", apiKey)
                    .build().toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("searchPoiInfo")) {
                Map<String, Object> searchPoiInfo = (Map<String, Object>) response.get("searchPoiInfo");
                Map<String, Object> pois = (Map<String, Object>) searchPoiInfo.get("pois");
                List<Map<String, Object>> poiList = (List<Map<String, Object>>) pois.get("poi");

                if (!poiList.isEmpty()) {
                    Map<String, Object> firstPoi = poiList.get(0);
                    double lat = Double.parseDouble((String) firstPoi.get("frontLat"));
                    double lon = Double.parseDouble((String) firstPoi.get("frontLon"));
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching coordinates for " + keyword + ": " + e.getMessage());
        }
        return new double[]{37.5665, 126.9780}; // 기본값 서울
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
                    "lang", 0, // 한국어
                    "format", "json",
                    "count", 5 // 5개의 추천 경로
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            
            return parseTmapResponse(response);

        } catch (Exception e) {
            System.err.println("Error calling Tmap API: " + e.getMessage());
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
                
                // Fare 정보 파싱
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

                    steps.add(RouteSearchStepResponse.builder()
                            .sequence(seq++)
                            .transportType(transportType)
                            .lineName(lineName != null ? lineName : (transportType == TransportType.WALK ? "도보" : "정보 없음"))
                            .lineId(lineId)
                            .startStationName(start != null ? (String) start.get("name") : "출발지")
                            .startStationId(start != null ? (String) start.get("stationId") : null)
                            .endStationName(end != null ? (String) end.get("name") : "도착지")
                            .endStationId(end != null ? (String) end.get("stationId") : null)
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
            System.err.println("Error parsing Tmap response: " + e.getMessage());
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