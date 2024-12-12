package com.ureca.daengggupayments.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY("결제 생성 직후 초기 상태"),
    IN_PROGRESS("결제 수단 및 소유자 인증 완료 상태"),
    WAITING_FOR_DEPOSIT("가상계좌 입금을 기다리는 상태"),
    DONE("결제가 승인 완료된 상태"),
    CANCELED("결제가 취소된 상태"),
    PARTIAL_CANCELED("결제가 부분적으로 취소된 상태"),
    ABORTED("결제 승인이 실패한 상태"),
    EXPIRED("결제 유효 시간이 초과되어 취소된 상태"),
    ERROR("결제 처리 중 에러가 발생한 상태");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
