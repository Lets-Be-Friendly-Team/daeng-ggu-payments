package com.ureca.daengggupayments.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelResponseDto {
    private String paymentKey;
    private String orderId;
    private String status;

    private BigDecimal cancelAmount;
    private String cancelReason;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
    private String cancelStatus;
    private String cancelRequestId;
}
