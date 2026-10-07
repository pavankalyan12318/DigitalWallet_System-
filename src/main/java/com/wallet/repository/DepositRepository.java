package com.wallet.repository;

import com.wallet.model.Deposit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
  Optional<Deposit> findByStripePaymentIntentId(String paymentIntentId);

  List<Deposit> findByUserIdOrderByCreatedAtDesc(Long userId);
}
