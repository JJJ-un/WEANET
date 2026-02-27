package com.weanet.server.api.report.dto;

import com.weanet.server.api.report.domain.Report;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "제보 응답 데이터")
public class ReportResponse {
    @Schema(description = "제보 ID")
    private Long id;
    
    @Schema(description = "제보 내용")
    private String content;
    
    @Schema(description = "제보자")
    private String reporter;
    
    @Schema(description = "제보 시간")
    private LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .content(report.getContent())
                .reporter(report.getReporter())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
