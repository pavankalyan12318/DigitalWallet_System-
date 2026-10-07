package com.wallet.controller;

import com.wallet.dto.DepositRequest;
import com.wallet.dto.DepositResponse;
import com.wallet.model.Deposit;
import com.wallet.model.Wallet;
import com.wallet.repository.DepositRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.service.DepositService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

  private final WalletRepository wallets;
  private final DepositRepository deposits;
  private final DepositService depositService;

  public WalletController(
      WalletRepository wallets, DepositRepository deposits, DepositService depositService) {
    this.wallets = wallets;
    this.deposits = deposits;
    this.depositService = depositService;
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<WalletView> me(Principal principal) {
    Wallet wallet =
        wallets
            .findByUserUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    return ResponseEntity.ok(
        new WalletView(wallet.getUser().getUsername(), wallet.getBalance().toPlainString()));
  }

  @PostMapping("/deposit")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<DepositResponse> deposit(
      Principal principal, @Valid @RequestBody DepositRequest request) throws Exception {
    return ResponseEntity.ok(depositService.createDepositIntent(principal.getName(), request));
  }

  @GetMapping("/deposits")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<DepositView>> deposits(Principal principal) {
    Wallet wallet =
        wallets
            .findByUserUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    List<DepositView> views =
        deposits.findByUserIdOrderByCreatedAtDesc(wallet.getUser().getId()).stream()
            .map(
                (Deposit deposit) ->
                    new DepositView(
                        deposit.getStripePaymentIntentId(),
                        deposit.getAmount().toPlainString(),
                        deposit.getCurrency(),
                        deposit.getStatus().name()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(views);
  }

  public static class WalletView {
    private String username;
    private String balance;

    public WalletView(String username, String balance) {
      this.username = username;
      this.balance = balance;
    }

    public String getUsername() {
      return username;
    }

    public String getBalance() {
      return balance;
    }
  }

  public static class DepositView {
    private String paymentIntentId;
    private String amount;
    private String currency;
    private String status;

    public DepositView(String paymentIntentId, String amount, String currency, String status) {
      this.paymentIntentId = paymentIntentId;
      this.amount = amount;
      this.currency = currency;
      this.status = status;
    }

    public String getPaymentIntentId() {
      return paymentIntentId;
    }

    public String getAmount() {
      return amount;
    }

    public String getCurrency() {
      return currency;
    }

    public String getStatus() {
      return status;
    }
  }
}
