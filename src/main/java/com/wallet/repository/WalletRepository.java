package com.wallet.repository;

import com.wallet.model.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
  Optional<Wallet> findByUserId(Long userId);

  Optional<Wallet> findByUserUsername(String username);
}
