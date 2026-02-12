package com.weanet.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CongestionService {

    private final RestTemplate restTemplate;
    private final Random random = new Random();

    @Value("${tmap.api.key}")
    private String tmapApiKey;

    @Value("${subway.congestion.api.url}")
    private String subwayApiUrl;

    @Value("${public.data.api.key}")
    private String publicApiKey;

    @Value("${bus.congestion.api.url}")
    private String busApiUrl;

    /**
     * 지하철 또는 버스의 실시간 혼잡도를 반환합니다.
     */
    public String getCongestion(String transportType, String lineId, String stationId) {
        if ("SUBWAY".equals(transportType)) {
            return getSubwayCongestion(lineId, stationId);
        } else if ("BUS".equals(transportType)) {
            return getBusCongestion(stationId);
        }
        return "정보 없음";
    }

    private String getSubwayCongestion(String lineId, String stationId) {
        if (isTmapMockMode()) return getMockCongestion();

        try {
            // SKT 지하철 혼잡도 API 호출 (실제로는 열차 번호 등이 필요할 수 있으나 간소화)
            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", tmapApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = UriComponentsBuilder.fromUriString(subwayApiUrl)
                    .pathSegment(lineId)
                    .pathSegment(stationId)
                    .toUriString();

            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            return parseSubwayCongestion(response);

        } catch (Exception e) {
            System.err.println("Error calling Subway Congestion API: " + e.getMessage());
            return getMockCongestion();
        }
    }

    private String getBusCongestion(String stationId) {
        if (isPublicMockMode()) return getMockCongestion();

        try {
            // 공공데이터 버스 도착 정보 API 호출
            String url = UriComponentsBuilder.fromUriString(busApiUrl)
                    .queryParam("serviceKey", publicApiKey)
                    .queryParam("stId", stationId)
                    .queryParam("resultType", "json")
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseBusCongestion(response);

        } catch (Exception e) {
            System.err.println("Error calling Bus Congestion API: " + e.getMessage());
            return getMockCongestion();
        }
    }

    private boolean isTmapMockMode() {
        return "YOUR_TMAP_API_KEY".equals(tmapApiKey) || tmapApiKey.isEmpty();
    }

    private boolean isPublicMockMode() {
        return "YOUR_PUBLIC_DATA_API_KEY".equals(publicApiKey) || publicApiKey.isEmpty();
    }

    private String parseSubwayCongestion(Map<String, Object> response) {
        try {
            if (response == null) return "정보 없음";

            // SKT Puzzle API 응답 구조: data -> congestionTrain 또는 contents -> stat -> [리스트]
            Object dataObj = response.get("data");
            int congestion = -1;

            if (dataObj instanceof Map<?, ?> dataMap) {
                Object conObj = dataMap.get("congestionTrain");
                if (conObj instanceof Number n) congestion = n.intValue();
            } else {
                Object contentsObj = response.get("contents");
                if (contentsObj instanceof Map<?, ?> contentsMap) {
                    Object statObj = contentsMap.get("stat");
                    if (statObj instanceof List<?> statList && !statList.isEmpty()) {
                        Object firstStat = statList.get(0);
                        if (firstStat instanceof Map<?, ?> firstStatMap) {
                            Object conObj = firstStatMap.get("congestionTrain");
                            if (conObj instanceof Number n) congestion = n.intValue();
                        }
                    }
                }
            }

            if (congestion == -1) return "정보 없음";

            // 혼잡도 판단 기준 (SKT 표준)
            if (congestion <= 34) return "여유";
            if (congestion <= 69) return "보통";
            return "혼잡";

        } catch (Exception e) {
            System.err.println("Error parsing Subway Congestion: " + e.getMessage());
            return "정보 없음";
        }
    }

    private String parseBusCongestion(Map<String, Object> response) {
        try {
            if (response == null) return "정보 없음";

            // 공공데이터 응답 구조: msgBody -> itemList -> [리스트]
            Object msgBodyObj = response.get("msgBody");
            if (msgBodyObj instanceof Map<?, ?> msgBody) {
                Object itemListObj = msgBody.get("itemList");
                if (itemListObj instanceof List<?> itemList && !itemList.isEmpty()) {
                    Object firstItem = itemList.get(0);
                    if (firstItem instanceof Map<?, ?> item) {
                        // rerideNum: 3(여유), 4(보통), 5(혼잡), 6(매우혼잡)
                        Object rerideObj = item.get("rerideNum1"); // 첫 번째 도착 예정 버스
                        if (rerideObj == null) rerideObj = item.get("rerideNum");
                        
                        String rerideStr = String.valueOf(rerideObj);
                        return switch (rerideStr) {
                            case "3" -> "여유";
                            case "4" -> "보통";
                            case "5", "6" -> "혼잡";
                            default -> "정보 없음";
                        };
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Bus Congestion: " + e.getMessage());
        }
        return "정보 없음";
    }

    private String getMockCongestion() {
        int level = random.nextInt(3);
        return switch (level) {
            case 0 -> "여유";
            case 1 -> "보통";
            default -> "혼잡";
        };
    }
}