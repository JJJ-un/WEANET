package com.weanet.server.service;

import com.weanet.server.domain.TransportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CongestionService {

    private final RestTemplate restTemplate;

    @Value("${tmap.api.key}")
    private String tmapApiKey;

    @Value("${public.data.api.key}")
    private String publicApiKey;

    @Value("${bus.congestion.api.url}")
    private String busApiUrl;

    /**
     * 지하철 또는 버스의 실시간 혼잡도를 반환합니다.
     */
    public String getCongestion(TransportType transportType, String lineId, String stationId) {
        if (TransportType.SUBWAY.equals(transportType)) {
            return getSubwayCongestion(lineId, stationId);
        } else if (TransportType.BUS.equals(transportType)) {
            return getBusCongestion(stationId);
        }
        return "정보 없음";
    }

    private String getSubwayCongestion(String lineId, String stationId) {
        if (stationId == null || stationId.isEmpty()) {
            return "정보 없음";
        }

        try {
            // SKT Puzzle 실시간 역 기준 혼잡도 API URL
            String url = UriComponentsBuilder.fromUriString("https://apis.openapi.sk.com/puzzle/subway/congestion/rltm/stat/stations")
                    .pathSegment(stationId)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", tmapApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            return parseSubwayCongestion(response);

        } catch (Exception e) {
            log.error("Subway Congestion API 호출 실패: {}", e.getMessage());
            return "정보 없음";
        }
    }

    private String getBusCongestion(String stationId) {
        if (stationId == null || stationId.isEmpty()) {
            return "정보 없음";
        }

        try {
            String url = UriComponentsBuilder.fromUriString(busApiUrl)
                    .queryParam("serviceKey", publicApiKey)
                    .queryParam("stId", stationId)
                    .queryParam("resultType", "json")
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseBusCongestion(response);

        } catch (Exception e) {
            log.error("Bus Congestion API 호출 실패: {}", e.getMessage());
            return "정보 없음";
        }
    }

    private String parseSubwayCongestion(Map<String, Object> response) {
        try {
            if (response == null) return "정보 없음";

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

            if (congestion <= 34) return "여유";
            if (congestion <= 69) return "보통";
            return "혼잡";

        } catch (Exception e) {
            log.error("Subway Congestion 파싱 에러: {}", e.getMessage());
            return "정보 없음";
        }
    }

    private String parseBusCongestion(Map<String, Object> response) {
        try {
            if (response == null) return "정보 없음";

            Object msgBodyObj = response.get("msgBody");
            if (msgBodyObj instanceof Map<?, ?> msgBody) {
                Object itemListObj = msgBody.get("itemList");
                if (itemListObj instanceof List<?> itemList && !itemList.isEmpty()) {
                    Object firstItem = itemList.get(0);
                    if (firstItem instanceof Map<?, ?> item) {
                        Object rerideObj = item.get("rerideNum1");
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
            log.error("Bus Congestion 파싱 에러: {}", e.getMessage());
        }
        return "정보 없음";
    }
}
