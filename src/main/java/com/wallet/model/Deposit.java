package com.wallet.model;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "deposits")
public class Deposit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, unique = true, length = 100)
  private String stripePaymentIntentId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 10)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DepositStatus status = DepositStatus.PENDING;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Deposit() {
  }

  public Deposit(User user, String stripePaymentIntentId, BigDecimal amount, String currency) {
    this.user = user;
    this.stripePaymentIntentId = stripePaymentIntentId;
    this.amount = amount;
    this.currency = currency;
    this.status = DepositStatus.PENDING;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getStripePaymentIntentId() {
    return stripePaymentIntentId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public DepositStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setStatus(DepositStatus status) {
    this.status = status;
  }
}
