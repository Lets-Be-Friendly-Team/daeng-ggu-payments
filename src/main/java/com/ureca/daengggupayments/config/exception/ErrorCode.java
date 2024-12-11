package com.ureca.daengggupayments.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 데이터 유효성 검사 관련 에러
    DATA_VALIDATION_ERROR(400, "입력된 데이터가 유효하지 않습니다.", 1000),
    AMOUNT_VALIDATION_ERROR(400, "금액 값이 결제 요청 시 보낸 데이터와 일치하지 않습니다.", 1001),

    // 데이터 조회 관련 에러
    DATA_NOT_EXIST(404, "조건에 맞는 데이터 정보가 없습니다.", 2000),

    // 결제 처리 관련 에러
    PAYMENT_PROCESS_FAILED(500, "결제 처리 중 문제가 발생했습니다.", 3000),
    PAYMENT_DATA_NOT_FOUND(404, "결제 데이터를 찾을 수 없습니다.", 3001),
    PAYMENT_API_ERROR(400, "결제 API 처리 과정에서 에러가 발생했습니다.", 3002),
    PAYMENT_STATUS_INVALID(400, "결제 상태 값이 잘못되었습니다.", 3003),
    CAN_NOT_CANCEL_PAYMENT(400, "결제 상태가 완료되지 않아 취소할 수 없습니다.", 3004),
    PAYMENT_HISTORY_NOT_FOUND(404, "결제 기록을 찾을 수 없습니다.", 3005), // 추가된 코드

    // 주문 처리 관련 에러
    ORDER_DATA_PROCESSING_ERROR(500, "주문 데이터 처리 과정에서 오류가 발생했습니다.", 4000);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
