package com.weanet.server.api.report;

import com.weanet.server.api.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByRouteIdOrderByCreatedAtDesc(Long routeId);
}
