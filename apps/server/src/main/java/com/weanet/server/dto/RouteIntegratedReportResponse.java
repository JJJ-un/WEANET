package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "대시보드 통합 리포트 응답")
public class RouteIntegratedReportResponse {
    @Schema(description = "경로 ID")
    private Long routeId;

    @Schema(description = "경로 별칭")
    private String routeName;

    @Schema(description = "통합 조언 메시지", example = "비가 오고 2호선이 혼잡하니 평소보다 10분 일찍 출발하세요!")
    private String integratedAdvice;

    @Schema(description = "구간별 요약 상태")
    private List<StepSummary> stepSummaries;

    @Schema(description = "최신 사용자 제보 리스트")
    private List<ReportResponse> recentReports;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepSummary {
        private String lineName;
        private String status; // 원활, 보통, 혼잡
        private String weatherIcon; 
        private String advice; // 구간별 짧은 조언
    }
}
