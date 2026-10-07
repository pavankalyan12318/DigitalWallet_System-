package com.wallet.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "wallets")
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance = BigDecimal.ZERO;

  protected Wallet() {
  }

  public Wallet(User user) {
    this.user = user;
    this.balance = BigDecimal.ZERO;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void credit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
