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
import java.util.concurrent.ConcurrentHashMap;

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

    @Value("${subway.congestion.api.url}")
    private String subwayApiUrl;

    // 역 이름 -> 역 코드 매핑 캐시 (성능 최적화용)
    private final Map<String, String> stationCodeCache = new ConcurrentHashMap<>();

    /**
     * 지하철 또는 버스의 실시간 혼잡도를 반환합니다.
     */
    public String getCongestion(TransportType transportType, String lineId, String stationId, String stationName, String lineName) {
        if (TransportType.SUBWAY.equals(transportType)) {
            return getSubwayCongestion(lineName, stationName);
        } else if (TransportType.BUS.equals(transportType)) {
            return getBusCongestion(stationId);
        }
        return "정보 없음";
    }

    private String getSubwayCongestion(String lineName, String stationName) {
        if (stationName == null || stationName.isEmpty()) return "정보 없음";

        try {
            // 1. 진짜 역 코드(stationCode) 찾기
            String stationCode = findStationCode(lineName, stationName);
            if (stationCode == null) {
                log.warn("역 코드를 찾을 수 없습니다: {} ({})", stationName, lineName);
                return "정보 없음";
            }

            // 2. 찾은 코드로 혼잡도 조회
            String url = UriComponentsBuilder.fromUriString(subwayApiUrl)
                    .pathSegment(stationCode)
                    .toUriString();

            log.info("==> [지하철 혼잡도 조회] 역: {}, 코드: {}, URL: {}", stationName, stationCode, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", tmapApiKey);
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            String result = parseSubwayCongestion(response);
            log.info("<== [지하철 혼잡도 결과] 역: {}, 결과: {}", stationName, result);
            return result;

        } catch (Exception e) {
            log.error("Subway Congestion API 호출 실패 (역: {}): {}", stationName, e.getMessage());
            return "정보 없음";
        }
    }

    /**
     * SKT Puzzle API를 통해 역 이름에 해당하는 3자리 역 코드를 찾습니다.
     */
    private String findStationCode(String lineName, String stationName) {
        String cacheKey = lineName + ":" + stationName;
        if (stationCodeCache.containsKey(cacheKey)) return stationCodeCache.get(cacheKey);

        try {
            // 정거장 정보 조회 API (이름으로 검색)
            String url = UriComponentsBuilder.fromUriString("https://apis.openapi.sk.com/puzzle/subway/congestion/stat/train/stations")
                    .queryParam("stationName", stationName.replace("역", "")) // "서울역" -> "서울"
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", tmapApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            
            if (response != null && response.containsKey("contents")) {
                Map<String, Object> contents = (Map<String, Object>) response.get("contents");
                List<Map<String, Object>> stations = (List<Map<String, Object>>) contents.get("stations");
                
                if (stations != null && !stations.isEmpty()) {
                    // 가장 유사한 노선의 역 코드를 선택 (간단한 매칭)
                    for (Map<String, Object> stat : stations) {
                        String code = String.valueOf(stat.get("stationCode"));
                        stationCodeCache.put(cacheKey, code);
                        return code;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("역 코드 검색 실패 ({}): {}", stationName, e.getMessage());
        }
        return null;
    }

    private String getBusCongestion(String stationId) {
        if (stationId == null || stationId.isEmpty()) return "정보 없음";

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
            if (response == null || !response.containsKey("data")) return "정보 없음";
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            
            Object conObj = data.get("congestionTrain");
            if (conObj instanceof Number n) {
                int congestion = n.intValue();
                if (congestion <= 34) return "여유";
                if (congestion <= 69) return "보통";
                return "혼잡";
            }
        } catch (Exception e) {
            log.error("Subway Congestion 파싱 에러: {}", e.getMessage());
        }
        return "정보 없음";
    }

    private String parseBusCongestion(Map<String, Object> response) {
        try {
            if (response == null) return "정보 없음";
            Object msgBodyObj = response.get("msgBody");
            if (msgBodyObj instanceof Map<?, ?> msgBody) {
                Object itemListObj = msgBody.get("itemList");
                if (itemListObj instanceof List<?> itemList && !itemList.isEmpty()) {
                    Map<String, Object> item = (Map<String, Object>) itemList.get(0);
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
        } catch (Exception e) {
            log.error("Bus Congestion 파싱 에러: {}", e.getMessage());
        }
        return "정보 없음";
    }
}
