package com.weanet.server.api.external.service;

import com.weanet.server.api.external.dto.SubwayRealtimeResponse;
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

    @Value("${public.data.api.key}")
    private String publicApiKey;

    /**
     * 철도종합정보시스템 공지사항(사고, 지연 등)을 조회합니다.
     */
    public List<SubwayRealtimeResponse> getSubwayAlerts(String lineName) {
        try {
            // 사용자 요청에 따른 정확한 주소로 변경
            String url = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B553766/ntce/getNotiList")
                    .queryParam("serviceKey", publicApiKey)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .queryParam("_type", "json")
                    .toUriString();

            log.info("지하철 공지사항 호출 URL: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseSubwayNoticeData(response, lineName);

        } catch (Exception e) {
            log.error("Subway Notice API 호출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<SubwayRealtimeResponse> parseSubwayNoticeData(Map<String, Object> response, String lineName) {
        List<SubwayRealtimeResponse> results = new ArrayList<>();
        
        try {
            if (response == null || !response.containsKey("response")) return results;
            
            Map<String, Object> resMap = (Map<String, Object>) response.get("response");
            Map<String, Object> body = (Map<String, Object>) resMap.get("body");
            if (body == null || !body.containsKey("items")) return results;
            
            Object itemsObj = body.get("items");
            List<Map<String, Object>> itemsList = new ArrayList<>();
            
            if (itemsObj instanceof Map) {
                itemsList = (List<Map<String, Object>>) ((Map) itemsObj).get("item");
            } else if (itemsObj instanceof List) {
                itemsList = (List<Map<String, Object>>) itemsObj;
            }

            for (Map<String, Object> item : itemsList) {
                String content = String.valueOf(item.get("drCont")); // 공지 내용
                String title = String.valueOf(item.get("drTitle"));   // 공지 제목
                
                if (title.contains(lineName) || content.contains(lineName)) {
                    results.add(SubwayRealtimeResponse.builder()
                            .lineName(lineName)
                            .arrivalMessage(title + ": " + content)
                            .isDelayed(content.contains("지연") || content.contains("장애") || content.contains("사고"))
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Subway Notice 파싱 에러: {}", e.getMessage());
        }

        return results;
    }
}
