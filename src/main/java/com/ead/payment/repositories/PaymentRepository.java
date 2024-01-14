package com.ead.payment.repositories;

import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentModel, UUID>, JpaSpecificationExecutor<PaymentModel> {
    Optional<PaymentModel> findTopByUserOrderByPaymentRequestDateDesc(UserModel userModel);
    @Query(value = "select * from tb_payments where user_user_id = :userId and payment_id = :paymentId", nativeQuery = true)
    Optional<PaymentModel> findPaymentByUser(@Param("userId") UUID userId,
                                             @Param("paymentId") UUID paymentId);
}
