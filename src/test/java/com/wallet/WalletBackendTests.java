package com.wallet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wallet.model.User;
import com.wallet.model.Wallet;
import com.wallet.repository.WalletRepository;
import com.wallet.service.QrCodeService;
import com.wallet.service.UserService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WalletBackendTests {

  @Autowired UserService userService;
  @Autowired WalletRepository wallets;
  @Autowired QrCodeService qrCodes;

  @Test
  void contextLoads() {}

  @Test
  void registerCreatesUniqueUserAndEmptyWallet() {
    User user = userService.register("alice", "password123");
    assertNotNull(user.getId());

    Wallet wallet =
        wallets.findByUserId(user.getId()).orElseThrow(() -> new IllegalStateException("no wallet"));
    assertEquals(0, BigDecimal.ZERO.compareTo(wallet.getBalance()));

    assertThrows(
        IllegalArgumentException.class, () -> userService.register("alice", "password123"));
  }

  @Test
  void qrCodeIsDeterministicPng() {
    byte[] first = qrCodes.qrCodeForUsername("alice");
    byte[] second = qrCodes.qrCodeForUsername("alice");
    assertArrayEquals(first, second);
  }
}
