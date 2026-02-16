package com.weanet.server.service;

import com.weanet.server.dto.SubwayRealtimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubwayService {

    private final RestTemplate restTemplate;

    @Value("${seoul.api.key}")
    private String apiKey;

    /**
     * 서울교통공사 공식 지하철 알림(사고, 지연 등)을 조회합니다.
     */
    public List<SubwayRealtimeResponse> getSubwayAlerts(String lineName) {
        try {
            String url = UriComponentsBuilder.fromUriString("http://apis.data.go.kr/B553766/smt-notice/subway-notice")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseSeoulAlertData(response, lineName);

        } catch (Exception e) {
            log.error("Subway Alert API 호출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<SubwayRealtimeResponse> parseSeoulAlertData(Map<String, Object> response, String lineName) {
        List<SubwayRealtimeResponse> results = new ArrayList<>();
        
        try {
            if (response == null || !response.containsKey("response")) return results;
            
            Map<String, Object> resMap = (Map<String, Object>) response.get("response");
            Map<String, Object> body = (Map<String, Object>) resMap.get("body");
            if (body == null || !body.containsKey("items")) return results;
            
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

            for (Map<String, Object> item : items) {
                String content = (String) item.get("cont");
                String title = (String) item.get("title");
                
                // title이나 content가 null인 경우 안전하게 건너뜀
                if (title == null || content == null) continue;
                
                if (title.contains(lineName) || content.contains(lineName)) {
                    results.add(SubwayRealtimeResponse.builder()
                            .lineName(lineName)
                            .arrivalMessage(content)
                            .isDelayed(content.contains("지연") || content.contains("장애"))
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Subway Alert 파싱 에러: {}", e.getMessage());
        }

        return results;
    }
}
