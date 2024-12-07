package com.ureca.daengggupayments.service;

import com.ureca.daengggupayments.dto.PaymentCancelResponseDto;
import com.ureca.daengggupayments.dto.PaymentRequestDto;
import com.ureca.daengggupayments.dto.PaymentResponseDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final WebClient tossPaymentsWebClient;

    public PaymentResponseDto confirmPayment(String paymentKey, String orderId, BigDecimal amount) {
        return tossPaymentsWebClient
                .post()
                .uri("/v1/payments/confirm")
                .bodyValue(buildPaymentRequest(paymentKey, orderId, amount))
                .retrieve()
                .bodyToMono(PaymentResponseDto.class)
                .doOnError(
                        WebClientResponseException.class,
                        ex -> {
                            log.error(
                                    "Error occurred while confirming payment. Status: {}, Body: {}",
                                    ex.getStatusCode(),
                                    ex.getResponseBodyAsString());
                        })
                .block();
    }

    private PaymentRequestDto buildPaymentRequest(
            String paymentKey, String orderId, BigDecimal amount) {
        return PaymentRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    public PaymentCancelResponseDto cancelPayment(String paymentKey, String cancelReason) {
        String uri = String.format("/v1/payments/%s/cancel", paymentKey);

        return tossPaymentsWebClient
                .post()
                .uri(uri)
                .bodyValue(cancelReason)
                .retrieve()
                .bodyToMono(PaymentCancelResponseDto.class)
                .doOnError(
                        WebClientResponseException.class,
                        ex -> {
                            log.error(
                                    "Error occurred during payment cancellation. Status: {}, Body: {}",
                                    ex.getStatusCode(),
                                    ex.getResponseBodyAsString());
                        })
                .block();
    }
}
