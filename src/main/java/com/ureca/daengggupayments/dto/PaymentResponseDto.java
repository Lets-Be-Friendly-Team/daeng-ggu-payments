package com.ureca.daengggupayments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentResponseDto {
    private String paymentKey;
    private String orderId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime approvedAt;
    private String receiptUrl;
    private String method;
    private String failure;
}
