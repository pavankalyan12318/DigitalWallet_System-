package com.wallet.controller;

import com.wallet.service.QrCodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final QrCodeService qrCodeService;

  public UserController(QrCodeService qrCodeService) {
    this.qrCodeService = qrCodeService;
  }

  @GetMapping("/{username}/qr")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<byte[]> qr(@PathVariable String username) {
    byte[] png = qrCodeService.qrCodeForUsername(username);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_PNG);
    return ResponseEntity.ok().headers(headers).body(png);
  }
}
