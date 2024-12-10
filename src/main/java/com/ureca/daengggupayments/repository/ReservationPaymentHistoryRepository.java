package com.ureca.daengggupayments.repository;

import com.ureca.daengggupayments.domain.ReservationPaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationPaymentHistoryRepository
        extends JpaRepository<ReservationPaymentHistory, Long> {}
