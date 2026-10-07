package com.wallet.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wallet.dto.DepositRequest;
import com.wallet.dto.DepositResponse;
import com.wallet.model.Deposit;
import com.wallet.model.User;
import com.wallet.repository.DepositRepository;
import com.wallet.repository.UserRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositService {

  private final UserRepository users;
  private final DepositRepository deposits;

  @Value("${stripe.currency-default:usd}")
  private String defaultCurrency;

  public DepositService(UserRepository users, DepositRepository deposits) {
    this.users = users;
    this.deposits = deposits;
  }

  @Transactional
  public DepositResponse createDepositIntent(String username, DepositRequest request)
      throws StripeException {
    User user =
        users
            .findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    String currency =
        request.getCurrency() != null ? request.getCurrency().toLowerCase() : defaultCurrency;
    long amountMinor = request.getAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

    Map<String, String> metadata = new HashMap<>();
    metadata.put("username", username);

    PaymentIntent intent =
        PaymentIntent.create(
            PaymentIntentCreateParams.builder()
                .setAmount(amountMinor)
                .setCurrency(currency)
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build())
                .build());

    Deposit deposit = new Deposit(user, intent.getId(), request.getAmount(), currency);
    deposits.save(deposit);
    return new DepositResponse(
        intent.getId(), intent.getClientSecret(), request.getAmount(), currency, "PENDING");
  }
}
