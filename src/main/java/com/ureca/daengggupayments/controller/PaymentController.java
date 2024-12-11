package com.ureca.daengggupayments.controller;

import com.ureca.daengggupayments.dto.OrderKeysAndAmountDto;
import com.ureca.daengggupayments.dto.PaymentCancelRequestDto;
import com.ureca.daengggupayments.dto.PaymentCancelResponseDto;
import com.ureca.daengggupayments.dto.PaymentRequestDto;
import com.ureca.daengggupayments.dto.PaymentResponseDto;
import com.ureca.daengggupayments.service.TossPaymentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPaymentService tossPaymentService;

    @PostMapping("/toss/confirm")
    public PaymentResponseDto confirmPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return tossPaymentService.confirmPayment(
                paymentRequestDto.getPaymentKey(),
                paymentRequestDto.getOrderId(),
                paymentRequestDto.getAmount());
    }

    @PostMapping("/toss/cancel")
    public PaymentCancelResponseDto cancelPayment(
            @RequestBody PaymentCancelRequestDto paymentCancelRequestDto) {
        return tossPaymentService.cancelPayment(
                paymentCancelRequestDto.getPaymentKey(), paymentCancelRequestDto.getCancelReason());
    }

    /**
     * 결제 서버로부터 전달받은 OrderKeysAndAmountDto 저장
     *
     * @param orderKeysAndAmountDto 결제 데이터
     * @return 저장 성공 메시지
     */
    @PostMapping("/orders")
    public ResponseEntity<String> saveOrderInfos(@RequestBody OrderKeysAndAmountDto orderKeysAndAmountDto) {
        tossPaymentService.saveOrderInfo(orderKeysAndAmountDto);
        return ResponseEntity.ok("Order information saved successfully.");
    }
}
