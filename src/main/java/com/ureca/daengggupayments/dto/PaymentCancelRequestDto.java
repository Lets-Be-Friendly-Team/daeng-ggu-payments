package com.ureca.daengggupayments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelRequestDto {
    private String paymentKey;
    private String cancelReason;
}
