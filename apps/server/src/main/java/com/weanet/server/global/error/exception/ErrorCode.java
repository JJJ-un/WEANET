package com.weanet.server.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부에 오류가 발생했습니다."),

    // 경로(Route) 관련
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "해당 경로를 찾을 수 없습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "R002", "외부 API 연동 중 오류가 발생했습니다."),

    // 날씨/혼잡도 관련
    WEATHER_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "날씨 정보를 불러올 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
