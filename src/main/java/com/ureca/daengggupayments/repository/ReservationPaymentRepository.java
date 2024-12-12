package com.ureca.daengggupayments.repository;

import com.ureca.daengggupayments.domain.ReservationPayment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
    // orderId로 ReservationPayment 찾기
    Optional<ReservationPayment> findByOrderId(String orderId);

    Optional<ReservationPayment> findByPaymentKey(String paymentKey);
}
