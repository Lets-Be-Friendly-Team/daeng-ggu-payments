package com.ureca.daengggupayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.daengggupayments.config.exception.ApiException;
import com.ureca.daengggupayments.config.exception.ErrorCode;
import com.ureca.daengggupayments.config.util.DateTimeUtil;
import com.ureca.daengggupayments.domain.PaymentStatus;
import com.ureca.daengggupayments.domain.ReservationPayment;
import com.ureca.daengggupayments.domain.ReservationPaymentHistory;
import com.ureca.daengggupayments.dto.OrderKeysAndAmountDto;
import com.ureca.daengggupayments.dto.PaymentCancelRequestDto;
import com.ureca.daengggupayments.dto.PaymentCancelResponseDto;
import com.ureca.daengggupayments.dto.PaymentErrorResponse;
import com.ureca.daengggupayments.dto.PaymentRequestDto;
import com.ureca.daengggupayments.dto.PaymentResponseDto;
import com.ureca.daengggupayments.dto.ReturnToServiceResponse;
import com.ureca.daengggupayments.repository.ReservationPaymentHistoryRepository;
import com.ureca.daengggupayments.repository.ReservationPaymentRepository;
import java.math.BigDecimal;
import java.util.Arrays;
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
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final ReservationPaymentHistoryRepository reservationPaymentHistoryRepository;

    public ReturnToServiceResponse confirmPayment(String paymentKey, String orderId, BigDecimal amount) {
        ReservationPayment reservationPayment = null;

        try {
            reservationPayment =
                    reservationPaymentRepository
                            .findByOrderId(orderId)
                            .orElseThrow(() -> new ApiException(ErrorCode.PAYMENT_DATA_NOT_FOUND));

            if (amount.compareTo(reservationPayment.getAmount()) != 0) {
                throw new ApiException(ErrorCode.AMOUNT_VALIDATION_ERROR);
            }

            // 결제 요청 및 응답 처리
            PaymentResponseDto paymentResponse =
                    tossPaymentsWebClient
                            .post()
                            .uri("/v1/payments/confirm")
                            .bodyValue(buildPaymentRequest(paymentKey, orderId, amount))
                            .retrieve()
                            .bodyToMono(PaymentResponseDto.class)
                            .block();

            reservationPayment.updatePaymentInfo(
                    paymentResponse.getPaymentKey(),
                    paymentResponse.getMethod(),
                    DateTimeUtil.parseToLocalDateTime(paymentResponse.getApprovedAt()));
            reservationPaymentRepository.save(reservationPayment);

            PaymentStatus status = parsePaymentStatus(paymentResponse.getStatus());

            // 결제 성공 상태 기록
            ReservationPaymentHistory paymentHistory =
                    ReservationPaymentHistory.builder()
                            .reservationPayment(reservationPayment)
                            .status(status)
                            .build();
            reservationPaymentHistoryRepository.save(paymentHistory);

            // ReturnToServiceResponse 객체로 변환 후 반환
            return ReturnToServiceResponse.builder()
                .paymentKey(paymentResponse.getPaymentKey())
                .orderId(paymentResponse.getOrderId())
                .status(paymentResponse.getStatus())
                .totalAmount(paymentResponse.getTotalAmount())
                .approvedAt(DateTimeUtil.parseToLocalDateTime(paymentResponse.getApprovedAt()))
                .receiptUrl(paymentResponse.getReceiptUrl())
                .method(paymentResponse.getMethod())
                .failure(paymentResponse.getFailure())
                .build();

        } catch (WebClientResponseException ex) {
            // 에러 응답 JSON 파싱
            PaymentErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString());

            if (reservationPayment != null) {
                ReservationPaymentHistory errorHistory =
                        ReservationPaymentHistory.builder()
                                .reservationPayment(reservationPayment)
                                .status(PaymentStatus.ERROR)
                                .errorLog(errorResponse.getMessage()) // 에러 메시지 저장
                                .build();
                reservationPaymentHistoryRepository.save(errorHistory);
            }

            throw new ApiException(ErrorCode.PAYMENT_API_ERROR);

        } catch (Exception e) {

            if (reservationPayment != null) {
                ReservationPaymentHistory errorHistory =
                        ReservationPaymentHistory.builder()
                                .reservationPayment(reservationPayment)
                                .status(PaymentStatus.ERROR)
                                .errorLog(e.getMessage())
                                .build();
                reservationPaymentHistoryRepository.save(errorHistory);
            }

            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }
    }

    private PaymentStatus parsePaymentStatus(String status) {
        return Arrays.stream(PaymentStatus.values())
                .filter(paymentStatus -> paymentStatus.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.PAYMENT_STATUS_INVALID));
    }

    private PaymentErrorResponse parseErrorResponse(String responseBody) {
        try {
            // JSON 문자열을 PaymentErrorResponse 객체로 변환
            return new ObjectMapper().readValue(responseBody, PaymentErrorResponse.class);
        } catch (Exception e) {
            // 파싱 실패 시 기본 에러 메시지 반환
            return new PaymentErrorResponse("UNKNOWN_ERROR", "Unable to parse error response");
        }
    }

    private PaymentRequestDto buildPaymentRequest(
            String paymentKey, String orderId, BigDecimal amount) {
        return PaymentRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    public PaymentCancelResponseDto cancelPayment(String orderId, String cancelReason) {
        ReservationPayment reservationPayment = null;

        try {
            // 데이터베이스에서 paymentKey로 결제 데이터 조회
            reservationPayment =
                    reservationPaymentRepository
                            .findByOrderId(orderId)
                            .orElseThrow(() -> new ApiException(ErrorCode.PAYMENT_DATA_NOT_FOUND));

            String paymentKey = reservationPayment.getPaymentKey();

            // 결제 취소 요청 및 응답 처리
            String uri = String.format("/v1/payments/%s/cancel", paymentKey);

            PaymentCancelResponseDto cancelResponse =
                    tossPaymentsWebClient
                            .post()
                            .uri(uri)
                            .bodyValue(
                                    PaymentCancelRequestDto.builder()
                                            .paymentKey(paymentKey)
                                            .cancelReason(cancelReason)
                                            .build())
                            .retrieve()
                            .bodyToMono(PaymentCancelResponseDto.class)
                            .block();

            System.out.println("응답 확인하기" + cancelResponse.toString());

            PaymentCancelResponseDto.CancelDetail cancelDetail = cancelResponse.getCancels().get(0);

            // 결제 취소 정보 업데이트
            reservationPayment.cancelPayment(
                    DateTimeUtil.parseToLocalDateTime(cancelDetail.getCanceledAt()));
            reservationPaymentRepository.save(reservationPayment);

            // 결제 취소 상태 기록
            ReservationPaymentHistory cancelHistory =
                    ReservationPaymentHistory.builder()
                            .reservationPayment(reservationPayment)
                            .status(PaymentStatus.CANCELED) // Enum 타입 사용
                            .cancelReason(cancelDetail.getCancelReason())
                            .build();
            reservationPaymentHistoryRepository.save(cancelHistory);

            return cancelResponse;

        } catch (WebClientResponseException ex) {
            PaymentErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString());

            if (reservationPayment != null) {
                ReservationPaymentHistory errorHistory =
                        ReservationPaymentHistory.builder()
                                .reservationPayment(reservationPayment)
                                .status(PaymentStatus.ERROR)
                                .errorLog(errorResponse.getMessage())
                                .build();
                reservationPaymentHistoryRepository.save(errorHistory);
            }

            throw new ApiException(ErrorCode.PAYMENT_API_ERROR);

        } catch (Exception e) {
            if (reservationPayment != null) {
                log.error(e.getMessage());
                ReservationPaymentHistory errorHistory =
                        ReservationPaymentHistory.builder()
                                .reservationPayment(reservationPayment)
                                .status(PaymentStatus.ERROR)
                                .errorLog(e.getMessage())
                                .build();
                reservationPaymentHistoryRepository.save(errorHistory);
            }

            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }
    }

    public void saveOrderInfo(OrderKeysAndAmountDto orderKeysAndAmountDto) {
        ReservationPayment reservationPayment = null;

        try {
            reservationPayment =
                    ReservationPayment.builder()
                            .customerKey(orderKeysAndAmountDto.getCustomerKey())
                            .orderId(orderKeysAndAmountDto.getOrderId())
                            .amount(orderKeysAndAmountDto.getAmount())
                            .build();

            reservationPayment = reservationPaymentRepository.save(reservationPayment);

            ReservationPaymentHistory paymentHistory =
                    ReservationPaymentHistory.builder()
                            .reservationPayment(reservationPayment)
                            .status(PaymentStatus.READY)
                            .build();

            reservationPaymentHistoryRepository.save(paymentHistory);

        } catch (Exception e) {
            if (reservationPayment != null) {
                ReservationPaymentHistory errorHistory =
                        ReservationPaymentHistory.builder()
                                .reservationPayment(reservationPayment)
                                .status(PaymentStatus.ERROR)
                                .errorLog(e.getMessage())
                                .build();

                reservationPaymentHistoryRepository.save(errorHistory);
            }
            throw new ApiException(ErrorCode.ORDER_DATA_PROCESSING_ERROR);
        }
    }

    public PaymentResponseDto designerConfirmPayment(
            String paymentKey, String orderId, BigDecimal amount) {
        try {
            PaymentResponseDto paymentResponse =
                    tossPaymentsWebClient
                            .post()
                            .uri("/v1/payments/confirm")
                            .bodyValue(buildPaymentRequest(paymentKey, orderId, amount))
                            .retrieve()
                            .bodyToMono(PaymentResponseDto.class)
                            .block();
            return paymentResponse;
        } catch (WebClientResponseException ex) {
            // HTTP 에러 응답 처리
            PaymentErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString());
            throw new ApiException(ErrorCode.PAYMENT_API_ERROR);

        } catch (Exception e) {
            // 기타 예외 처리
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }
    }
}
