package com.wallet.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.wallet.service.StripeWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

  private final StripeWebhookService webhookService;
  private final String webhookSecret;

  public StripeWebhookController(
      StripeWebhookService webhookService,
      @Value("${stripe.webhook-secret}") String webhookSecret) {
    this.webhookService = webhookService;
    this.webhookSecret = webhookSecret;
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> webhook(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
    } catch (SignatureVerificationException ex) {
      return ResponseEntity.badRequest().body("Invalid signature");
    }
    if (event.getType() != null && event.getType().startsWith("payment_intent.")) {
      PaymentIntent intent =
          (PaymentIntent)
              event.getDataObjectDeserializer().getObject().orElse(null);
      if (intent != null) {
        webhookService.handlePaymentIntent(intent);
      }
    }
    return ResponseEntity.ok("received");
  }
}
