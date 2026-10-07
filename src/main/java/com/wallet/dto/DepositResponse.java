package com.wallet.dto;

import java.math.BigDecimal;

public class DepositResponse {

  private String paymentIntentId;
  private String clientSecret;
  private BigDecimal amount;
  private String currency;
  private String status;

  public DepositResponse(
      String paymentIntentId, String clientSecret, BigDecimal amount, String currency, String status) {
    this.paymentIntentId = paymentIntentId;
    this.clientSecret = clientSecret;
    this.amount = amount;
    this.currency = currency;
    this.status = status;
  }

  public String getPaymentIntentId() {
    return paymentIntentId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getStatus() {
    return status;
  }
}
