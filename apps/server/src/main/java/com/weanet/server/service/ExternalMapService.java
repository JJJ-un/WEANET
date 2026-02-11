package com.weanet.server.service;

import com.weanet.server.dto.RouteSearchResponse;
import com.weanet.server.dto.RouteStepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        if (isMockMode()) {
            return getMockRoutes();
        }

        try {
            // Tmap API는 POST 요청을 권장하는 경우가 많으나, Transit API 스펙에 맞춰 구현 필요
            // 여기서는 표준적인 Transit API 호출 구조를 따릅니다.
            Map<String, Object> requestBody = Map.of(
                    "startX", String.valueOf(startLng),
                    "startY", String.valueOf(startLat),
                    "endX", String.valueOf(endLng),
                    "endY", String.valueOf(endLat),
                    "lang", 0, // 한국어
                    "format", "json",
                    "count", 5 // 5개의 추천 경로
            );

            Map<String, Object> response = restTemplate.postForObject(apiUrl, requestBody, Map.class);
            return parseTmapResponse(response);

        } catch (Exception e) {
            System.err.println("Error calling Tmap API: " + e.getMessage());
            return getMockRoutes();
        }
    }

    private boolean isMockMode() {
        return "YOUR_TMAP_API_KEY".equals(apiKey) || apiKey.isEmpty();
    }

    private List<RouteSearchResponse> parseTmapResponse(Map<String, Object> response) {
        List<RouteSearchResponse> results = new ArrayList<>();
        
        if (response == null || !response.containsKey("metaData")) return results;
        
        Map<String, Object> metaData = (Map<String, Object>) response.get("metaData");
        Map<String, Object> plan = (Map<String, Object>) metaData.get("plan");
        List<Map<String, Object>> itineraries = (List<Map<String, Object>>) plan.get("itineraries");

        for (Map<String, Object> itinerary : itineraries) {
            int totalTime = ((Number) itinerary.get("totalTime")).intValue() / 60; // 초 -> 분 변환
            int totalFare = ((Number) ((Map<String, Object>) itinerary.get("fare")).get("regular")).get("totalFare").intValue();
            int transferCount = ((Number) itinerary.get("transferCount")).intValue();
            
            List<Map<String, Object>> legs = (List<Map<String, Object>>) itinerary.get("legs");
            List<RouteStepResponse> steps = new ArrayList<>();
            StringBuilder summary = new StringBuilder();

            int seq = 1;
            for (Map<String, Object> leg : legs) {
                String mode = (String) leg.get("mode");
                if ("WALK".equals(mode)) continue; // 대시보드용으로는 대중교통 구간 위주로 구성

                Map<String, Object> routeInfo = (Map<String, Object>) leg.get("routeInfo");
                String lineName = (String) routeInfo.get("name");
                
                if (summary.length() > 0) summary.append(" -> ");
                summary.append(lineName);

                steps.add(RouteStepResponse.builder()
                        .sequence(seq++)
                        .transportType(mode)
                        .lineName(lineName)
                        .startStationName((String) leg.get("startName"))
                        .endStationName((String) leg.get("endName"))
                        .sectionTime(((Number) leg.get("sectionTime")).intValue() / 60)
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

        return results;
    }

    private List<RouteSearchResponse> getMockRoutes() {
        List<RouteSearchResponse> results = new ArrayList<>();
        results.add(RouteSearchResponse.builder()
                .totalTime(45)
                .totalFare(1250)
                .transferCount(1)
                .summary("지하철 2호선 -> 신분당선 (Mock)")
                .steps(createMockStepsForRoute1())
                .build());
        return results;
    }

    private List<RouteStepResponse> createMockStepsForRoute1() {
        List<RouteStepResponse> steps = new ArrayList<>();
        steps.add(RouteStepResponse.builder()
                .sequence(1)
                .transportType("SUBWAY")
                .lineName("2호선")
                .startStationName("강남역")
                .endStationName("양재역")
                .sectionTime(10)
                .build());
        steps.add(RouteStepResponse.builder()
                .sequence(2)
                .transportType("SUBWAY")
                .lineName("신분당선")
                .startStationName("양재역")
                .endStationName("판교역")
                .sectionTime(15)
                .build());
        return steps;
    }
}