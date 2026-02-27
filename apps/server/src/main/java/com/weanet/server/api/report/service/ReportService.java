package com.weanet.server.api.report.service;

import com.weanet.server.api.report.domain.Report;
import com.weanet.server.api.route.domain.Route;
import com.weanet.server.api.report.dto.ReportRequest;
import com.weanet.server.api.report.dto.ReportResponse;
import com.weanet.server.api.report.ReportRepository;
import com.weanet.server.api.route.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final RouteRepository routeRepository;

    @Transactional
    public ReportResponse createReport(Long routeId, ReportRequest request) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 찾을 수 없습니다."));
        
        Report report = Report.builder()
                .route(route)
                .content(request.getContent())
                .reporter(request.getReporter())
                .build();
        
        return ReportResponse.from(reportRepository.save(report));
    }

    public List<ReportResponse> getReportsByRoute(Long routeId) {
        return reportRepository.findByRouteIdOrderByCreatedAtDesc(routeId).stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }
}
