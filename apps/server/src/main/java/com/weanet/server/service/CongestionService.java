package com.weanet.server.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CongestionService {

    private final Random random = new Random();

    /**
     * 노선 ID와 역 ID를 기반으로 실시간 혼잡도를 반환합니다.
     * 실제 서비스에서는 서울시 실시간 지하철 정보 또는 SKT 혼잡도 API를 호출합니다.
     */
    public String getCongestion(String lineId, String stationId) {
        // Mock 로직: 0~2 사이의 숫자를 생성하여 혼잡도 결정
        int level = random.nextInt(3);
        return switch (level) {
            case 0 -> "여유";
            case 1 -> "보통";
            default -> "혼잡";
        };
    }
}
