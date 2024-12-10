package com.ureca.daengggupayments.repository;

import com.ureca.daengggupayments.domain.ReservationPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {}
