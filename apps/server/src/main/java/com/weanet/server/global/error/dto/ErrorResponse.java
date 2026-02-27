package com.weanet.server.global.error.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "공통 에러 응답 객체")
public class ErrorResponse {
    @Schema(description = "에러 시각", example = "2026-02-12T14:30:00")
    private final LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;

    @Schema(description = "에러 코드 (커스텀)", example = "ROUTE_NOT_FOUND")
    private final String code;

    @Schema(description = "사용자용 에러 메시지", example = "해당 경로를 찾을 수 없습니다.")
    private final String message;
}
