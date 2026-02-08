package com.weanet.server.controller;

import com.weanet.server.dto.ReportRequest;
import com.weanet.server.dto.ReportResponse;
import com.weanet.server.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes/{routeId}/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "제보 관리 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "제보 작성", description = "특정 경로에 대한 실시간 상황을 제보합니다.")
    public ReportResponse createReport(@PathVariable Long routeId, @RequestBody ReportRequest request) {
        return reportService.createReport(routeId, request);
    }

    @GetMapping
    @Operation(summary = "경로별 제보 조회", description = "특정 경로에 등록된 최신 제보 목록을 조회합니다.")
    public List<ReportResponse> getReports(@PathVariable Long routeId) {
        return reportService.getReportsByRoute(routeId);
    }
}
