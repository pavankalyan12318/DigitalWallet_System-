package com.wallet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.wallet.service.StripeWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

  private final StripeWebhookService webhookService;
  private final String webhookSecret;
  private final ObjectMapper objectMapper;

  public StripeWebhookController(
          StripeWebhookService webhookService,
          @Value("${stripe.webhook-secret}") String webhookSecret,
          ObjectMapper objectMapper) {

    this.webhookService = webhookService;
    this.webhookSecret = webhookSecret;
    this.objectMapper = objectMapper;
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> webhook(
          @RequestBody String payload,
          @RequestHeader("Stripe-Signature") String sigHeader) {

    System.out.println("===== WEBHOOK RECEIVED =====");

    Event event;

    // 1. Verify Stripe webhook signature
    try {

      event = Webhook.constructEvent(
              payload,
              sigHeader,
              webhookSecret
      );

      System.out.println("Event type: " + event.getType());

    } catch (SignatureVerificationException ex) {

      System.out.println("===== SIGNATURE FAILED =====");

      return ResponseEntity
              .badRequest()
              .body("Invalid signature");
    }

    // 2. Handle PaymentIntent events
    if (event.getType() != null
            && event.getType().startsWith("payment_intent.")) {

      try {

        // Read the webhook JSON
        JsonNode root = objectMapper.readTree(payload);

        // Get PaymentIntent ID
        String paymentIntentId =
                root.path("data")
                        .path("object")
                        .path("id")
                        .asText();

        System.out.println(
                "PaymentIntent ID: " + paymentIntentId
        );

        // Make sure we actually got an ID
        if (paymentIntentId == null
                || paymentIntentId.isEmpty()) {

          System.out.println(
                  "PaymentIntent ID is empty"
          );

          return ResponseEntity
                  .badRequest()
                  .body("PaymentIntent ID missing");
        }

        // Retrieve the PaymentIntent from Stripe
        PaymentIntent intent =
                PaymentIntent.retrieve(paymentIntentId);

        System.out.println(
                "PaymentIntent status: "
                        + intent.getStatus()
        );

        // Send PaymentIntent to service
        webhookService.handlePaymentIntent(intent);

        System.out.println(
                "===== WEBHOOK PROCESSING COMPLETED ====="
        );

      } catch (Exception ex) {

        ex.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body("Webhook processing failed");
      }
    }

    return ResponseEntity
            .ok("received");
  }
}