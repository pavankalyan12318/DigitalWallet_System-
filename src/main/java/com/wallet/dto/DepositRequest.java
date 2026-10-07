package com.wallet.dto;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class DepositRequest {

  @NotNull
  @DecimalMin(value = "0.50", message = "Minimum deposit is 0.50")
  private BigDecimal amount;

  @Pattern(regexp = "^[a-z]{3}$", message = "Currency must be a 3-letter ISO code")
  private String currency = "usd";

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
