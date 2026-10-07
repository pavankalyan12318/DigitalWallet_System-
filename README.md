# Digital Wallet Backend

Spring Boot backend for website wallets with unique usernames, QR codes,
Stripe PaymentIntents deposits, Stripe webhooks with `@Transactional` balance updates,
and Spring Security JWT role-based auth.

Location: standalone project at `/tmp/wallet-backend` (not in any internal repo).

## Stack

- Java 11, Spring Boot 2.7.18, Maven
- Postgres (runtime), H2 (tests)
- Spring Security + JJWT, Stripe Java SDK, ZXing QR codes

## Configure

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/wallet
export DATABASE_USER=wallet
export DATABASE_PASSWORD=wallet
export JWT_SECRET=change-me-to-a-long-random-secret-for-dev-only
export STRIPE_API_KEY=sk_test_placeholder
export STRIPE_WEBHOOK_SECRET=whsec_placeholder
```

Create the database:

```bash
createdb wallet
```

## Run

Maven is required. Verification status on the scaffolding host: NOT compiled,
NOT test-run, and the HTTP flow (register/login/wallet/deposit/webhook/QR) NOT
exercised here — `mvn` is not installed, Maven Central is unreachable from this
host (connection timeout), and no Postgres/Docker service is available. Stripe
keys are placeholders, so deposit and webhook calls need real keys plus
`mvn test` / `mvn spring-boot:run` on a connected machine.

```bash
cd /tmp/wallet-backend
mvn spring-boot:run
```

## API

- `POST /api/auth/register` — `{username, password}` returns JWT.
- `POST /api/auth/login` — `{username, password}` returns JWT.
- `GET /api/wallets/me` — JWT `USER` role, returns balance.
- `POST /api/wallets/deposit` — JWT, `{amount, currency}` creates a Stripe
  PaymentIntent and a `PENDING` deposit, returns `clientSecret`.
- `GET /api/wallets/deposits` — JWT, lists deposits for the caller.
- `GET /api/users/{username}/qr` — JWT, returns the user QR code PNG.
- `POST /api/stripe/webhook` — public, Stripe-signed. Verifies
  `Stripe-Signature` with `STRIPE_WEBHOOK_SECRET`, then atomically credits the
  wallet on `payment_intent.succeeded` via `@Transactional`.

## Notes

- Usernames are unique. Each user gets one wallet and a QR payload
  `wallet:user:{username}`.
- Webhook crediting is idempotent: an already-`SUCCEEDED` deposit is not
  credited twice.
- Wallet and user endpoints require the `USER` role; auth and Stripe webhook endpoints are public.
