package com.wallet.service;

import com.wallet.model.User;
import com.wallet.model.Wallet;
import com.wallet.repository.UserRepository;
import com.wallet.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository users;
  private final WalletRepository wallets;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository users, WalletRepository wallets, PasswordEncoder passwordEncoder) {
    this.users = users;
    this.wallets = wallets;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User register(String username, String password) {
    if (users.existsByUsername(username)) {
      throw new IllegalArgumentException("Username is already taken: " + username);
    }
    User user = new User(username, passwordEncoder.encode(password), "USER");
    User saved = users.save(user);
    wallets.save(new Wallet(saved));
    return saved;
  }

  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return users
        .findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
  }
}
