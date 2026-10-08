package com.wallet.service;

import com.stripe.model.PaymentIntent;
import com.wallet.model.Deposit;
import com.wallet.model.DepositStatus;
import com.wallet.model.Wallet;
import com.wallet.repository.DepositRepository;
import com.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeWebhookService {

  private final DepositRepository deposits;
  private final WalletRepository wallets;

  public StripeWebhookService(DepositRepository deposits, WalletRepository wallets) {
    this.deposits = deposits;
    this.wallets = wallets;
  }

  @Transactional
  public void handlePaymentIntent(PaymentIntent intent) {

    System.out.println("===== STRIPE WEBHOOK =====");
    System.out.println("PaymentIntent ID: " + intent.getId());
    System.out.println("PaymentIntent status: " + intent.getStatus());
    System.out.println("==========================");


    String status = intent.getStatus();
    if ("succeeded".equals(status)) {
      applySucceeded(intent.getId());
    } else if ("payment_failed".equals(status)
        || "canceled".equals(status)
        || "requires_payment_method".equals(status)) {
      markFailed(intent.getId());
    }
  }

  private void applySucceeded(String paymentIntentId) {
    Deposit deposit =
        deposits
            .findByStripePaymentIntentId(paymentIntentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Unknown payment intent: " + paymentIntentId));
    if (deposit.getStatus() == DepositStatus.SUCCEEDED) {
      return;
    }
    Wallet wallet =
        wallets
            .findByUserId(deposit.getUser().getId())
            .orElseThrow(() -> new IllegalStateException("Wallet missing for deposit"));
    wallet.credit(deposit.getAmount());
    deposit.setStatus(DepositStatus.SUCCEEDED);
    wallets.save(wallet);
    deposits.save(deposit);
  }

  private void markFailed(String paymentIntentId) {
    deposits
        .findByStripePaymentIntentId(paymentIntentId)
        .ifPresent(
            deposit -> {
              deposit.setStatus(DepositStatus.FAILED);
              deposits.save(deposit);
            });
  }
}
