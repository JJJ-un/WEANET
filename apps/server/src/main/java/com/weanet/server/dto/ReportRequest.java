package com.weanet.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "제보 작성 요청")
public class ReportRequest {
    @Schema(description = "제보 내용", example = "지금 2호선 강남역 방향 사람 엄청 많아요!")
    private String content;
    
    @Schema(description = "제보자 (익명)", example = "익명의 뚜벅이")
    private String reporter;
}
