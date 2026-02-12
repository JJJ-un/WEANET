package com.weanet.server.service;

import com.weanet.server.dto.SubwayRealtimeResponse;
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
public class SubwayService {

    private final RestTemplate restTemplate;

    @Value("${seoul.api.key}")
    private String apiKey;

    /**
     * 서울교통공사 공식 지하철 알림(사고, 지연 등)을 조회합니다.
     */
    public List<SubwayRealtimeResponse> getSubwayAlerts(String lineName) {
        if (isMockMode()) {
            return getMockAlerts(lineName);
        }

        try {
            // 서울교통공사 지하철 알림정보 API URL (공공데이터포털)
            String url = UriComponentsBuilder.fromUriString("http://apis.data.go.kr/B553766/smt-notice/subway-notice")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseSeoulAlertData(response, lineName);

        } catch (Exception e) {
            System.err.println("Error calling Seoul Subway Alert API: " + e.getMessage());
            return getMockAlerts(lineName);
        }
    }

    private boolean isMockMode() {
        return "YOUR_SEOUL_API_KEY".equals(apiKey) || apiKey.isEmpty();
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
                String content = (String) item.get("cont"); // 알림 내용
                String title = (String) item.get("title"); // 알림 제목
                
                // 현재 확인 중인 노선 이름이 제목이나 내용에 포함되어 있는지 확인
                if (title.contains(lineName) || content.contains(lineName)) {
                    results.add(SubwayRealtimeResponse.builder()
                            .lineName(lineName)
                            .arrivalMessage(content) // 공식 알림 내용을 메시지로 사용
                            .isDelayed(content.contains("지연") || content.contains("장애"))
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Seoul Subway Alert data: " + e.getMessage());
        }

        return results;
    }

    private List<SubwayRealtimeResponse> getMockAlerts(String lineName) {
        // 실제 상황을 가정하여 2호선일 때만 지연 공지 가작 데이터 생성
        if ("2호선".equals(lineName)) {
            return List.of(SubwayRealtimeResponse.builder()
                    .lineName("2호선")
                    .arrivalMessage("[공식] 2호선 신호 고장으로 인해 전 구간 15분 내외 지연 운행 중입니다.")
                    .isDelayed(true)
                    .build());
        }
        return new ArrayList<>();
    }
}
