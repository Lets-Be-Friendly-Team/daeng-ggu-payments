package com.ureca.daengggupayments.service;

import com.ureca.daengggupayments.dto.PaymentRequestDto;
import com.ureca.daengggupayments.dto.PaymentResponseDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final WebClient tossPaymentsWebClient;

    public Mono<PaymentResponseDto> confirmPayment(
            String paymentKey, String orderId, BigDecimal amount) {
        return tossPaymentsWebClient
                .post()
                .uri("/v1/payments/confirm")
                .bodyValue(buildPaymentRequest(paymentKey, orderId, amount))
                .retrieve()
                .bodyToMono(PaymentResponseDto.class);
    }

    private PaymentRequestDto buildPaymentRequest(
            String paymentKey, String orderId, BigDecimal amount) {
        return PaymentRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
