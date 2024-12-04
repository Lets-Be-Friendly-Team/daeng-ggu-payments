package com.ureca.daengggupayments.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DATA_VALIDATION_ERROR(400, "입력된 데이터가 유효하지 않습니다.", 1000),
    DATA_NOT_EXIST(500, "조건에 맞는 데이터 정보가 없습니다.", 2000);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
