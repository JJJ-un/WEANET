package com.weanet.server.service;

import com.weanet.server.dto.RouteSearchResponse;
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
                List<RouteStepResponse> steps = new ArrayList<>();
                StringBuilder summary = new StringBuilder();

                int seq = 1;
                for (Map<String, Object> leg : legs) {
                    String mode = (String) leg.get("mode");
                    // 도보 구간은 요약에 포함하지 않지만 정보는 필요할 수 있음
                    if ("WALK".equals(mode)) continue;

                    Map<String, Object> routeInfo = (Map<String, Object>) leg.get("routeInfo");
                    if (routeInfo == null) continue;
                    
                    String lineName = (String) routeInfo.get("name");
                    // 실시간 혼잡도 조회를 위한 ID (노선 ID 또는 버스 번호)
                    String lineId = (String) routeInfo.get("routeId"); 
                    
                    if (summary.length() > 0) summary.append(" -> ");
                    summary.append(lineName);

                    steps.add(RouteStepResponse.builder()
                            .sequence(seq++)
                            .transportType(mode)
                            .lineName(lineName)
                            .lineId(lineId)
                            .startStationName((String) leg.get("startName"))
                            .startStationId((String) ((Map<String, Object>) leg.get("start")).get("stationId"))
                            .endStationName((String) leg.get("endName"))
                            .endStationId((String) ((Map<String, Object>) leg.get("end")).get("stationId"))
                            .sectionTime(((Number) leg.get("sectionTime")).intValue() / 60)
                            .lat(((Number) ((Map<String, Object>) leg.get("start")).get("lat")).doubleValue())
                            .lng(((Number) ((Map<String, Object>) leg.get("start")).get("lon")).doubleValue())
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
}