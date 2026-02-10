package com.weanet.server.service;

import com.weanet.server.dto.RouteSearchResponse;
import com.weanet.server.dto.RouteStepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalMapService {

    /**
     * Tmap 대중교통 경로 검색 API를 모사한 Mock 로직입니다.
     * 출발지와 도착지 좌표를 기반으로 추천 경로 리스트를 반환합니다.
     */
    public List<RouteSearchResponse> searchRoutes(double startLat, double startLng, double endLat, double endLng) {
        List<RouteSearchResponse> results = new ArrayList<>();

        // 가상의 추천 경로 1: 지하철 중심
        results.add(RouteSearchResponse.builder()
                .totalTime(45)
                .totalFare(1250)
                .transferCount(1)
                .summary("지하철 2호선 -> 신분당선")
                .steps(createMockStepsForRoute1())
                .build());

        // 가상의 추천 경로 2: 버스 중심
        results.add(RouteSearchResponse.builder()
                .totalTime(55)
                .totalFare(1200)
                .transferCount(0)
                .summary("버스 143번")
                .steps(createMockStepsForRoute2())
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

    private List<RouteStepResponse> createMockStepsForRoute2() {
        List<RouteStepResponse> steps = new ArrayList<>();
        steps.add(RouteStepResponse.builder()
                .sequence(1)
                .transportType("BUS")
                .lineName("143번")
                .startStationName("강남역")
                .endStationName("판교역")
                .sectionTime(50)
                .build());
        return steps;
    }
}
