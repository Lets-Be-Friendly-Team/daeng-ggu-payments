package com.ureca.daengggupayments.service;

import com.ureca.daengggupayments.dto.PaymentRequestDto;
import com.ureca.daengggupayments.dto.PaymentResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
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
            .bodyToMono(Map.class)
            .map(responseMap -> {
                return PaymentResponseDto.builder()
                    .paymentKey((String) responseMap.get("paymentKey"))
                    .orderId((String) responseMap.get("orderId"))
                    .status((String) responseMap.get("status"))
                    .totalAmount(new BigDecimal(String.valueOf(responseMap.get("totalAmount"))))
                    .approvedAt(LocalDateTime.parse((String) responseMap.get("approvedAt")))
                    .receiptUrl((String) ((Map<?, ?>) responseMap.get("receipt")).get("url"))
                    .method((String) responseMap.get("method"))
                    .failure((String) responseMap.get("failure"))
                    .build();
            })
            .doOnError(WebClientResponseException.class, ex -> {
                log.error("Error occurred while confirming payment. Status: {}, Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
            });
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
