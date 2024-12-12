package com.ureca.daengggupayments.controller;

import com.ureca.daengggupayments.dto.OrderIdWithCancelReasonDto;
import com.ureca.daengggupayments.dto.OrderKeysAndAmountDto;
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

/** 결제와 관련된 요청을 처리하는 REST 컨트롤러. */
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPaymentService tossPaymentService;

    /**
     * 결제 승인을 처리하는 API.
     *
     * @param paymentRequestDto 결제 승인 요청에 필요한 정보(paymentKey, orderId, amount)를 포함한 DTO.
     * @return 결제 승인 결과를 담고 있는 {@link PaymentResponseDto}.
     */
    @PostMapping("/toss/confirm")
    public PaymentResponseDto confirmPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return tossPaymentService.confirmPayment(
                paymentRequestDto.getPaymentKey(),
                paymentRequestDto.getOrderId(),
                paymentRequestDto.getAmount());
    }

    /**
     * 결제 취소를 처리하는 API.
     *
     * @param orderIdWithCancelReasonDto 결제 취소 요청에 필요한 정보(orderId와 취소 사유)를 포함한 DTO.
     * @return 결제 취소 결과를 담고 있는 {@link PaymentCancelResponseDto}.
     */
    @PostMapping("/toss/cancel")
    public PaymentCancelResponseDto cancelPayment(
            @RequestBody OrderIdWithCancelReasonDto orderIdWithCancelReasonDto) {
        return tossPaymentService.cancelPayment(
                orderIdWithCancelReasonDto.getOrderId(),
                orderIdWithCancelReasonDto.getCancelReason());
    }

    /**
     * 결제 서버에서 전달받은 주문 정보를 저장하는 API.
     *
     * @param orderKeysAndAmountDto 결제에 필요한 customerKey, orderId, amount 정보를 포함한 DTO.
     * @return 주문 정보 저장 성공 메시지를 포함한 {@link ResponseEntity}.
     */
    @PostMapping("/orders")
    public ResponseEntity<String> saveOrderInfos(
            @RequestBody OrderKeysAndAmountDto orderKeysAndAmountDto) {
        tossPaymentService.saveOrderInfo(orderKeysAndAmountDto);
        return ResponseEntity.ok("주문 정보가 성공적으로 저장되었습니다.");
    }
}
