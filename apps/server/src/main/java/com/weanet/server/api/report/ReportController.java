package com.weanet.server.api.report;

import com.weanet.server.api.report.service.ReportService;
import com.weanet.server.api.report.dto.ReportRequest;
import com.weanet.server.api.report.dto.ReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "사용자 제보 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "제보 생성", description = "특정 노선의 혼잡도나 지연 상황을 제보합니다.")
    public ReportResponse createReport(@RequestBody ReportRequest request) {
        return reportService.createReport(request);
    }

    @GetMapping("/route/{routeId}")
    @Operation(summary = "경로별 제보 조회", description = "특정 경로와 관련된 최신 제보 목록을 조회합니다.")
    public List<ReportResponse> getReportsByRoute(@PathVariable Long routeId) {
        return reportService.getReportsByRoute(routeId);
    }
}
