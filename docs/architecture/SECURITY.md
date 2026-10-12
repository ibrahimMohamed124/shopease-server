# Security Rules

## Authentication and token lifecycle

First inspect the legacy implementation and document the current behavior before changing it.

- Preserve intended access-token and refresh-token behavior unless an intentional change is approved.
- Validate signature, expiry, issuer, audience, and token type where applicable.
- Define refresh-token rotation, reuse detection, revocation, logout, and expiration behavior.
- Store refresh tokens safely; do not expose them in logs.
- Use a safe password hashing algorithm supported by the selected Spring Security configuration.
- If legacy password hashes must remain valid, design and test a safe migration/upgrade path. Never silently reset or invalidate all users.
- Password-reset tokens must be random, short-lived, single-use, and stored/compared safely.
- Avoid account-enumeration leaks in authentication and password-reset responses where appropriate.

## Authorization

- Authorization must be enforced on the backend.
- Review every endpoint and use case for public, authenticated, owner-only, or role-based access.
- Object-level authorization is required: a user must not access another user's cart, order, address, return, or review by changing an ID.
- Administrative APIs must not rely on hidden frontend controls.
- Prefer centralized, understandable authorization policies, but do not put every domain rule into one giant security configuration.
- Do not grant broad roles to make tests pass.

## Validation and error handling

- Validate request shapes at API boundaries using Bean Validation.
- Enforce business invariants in the domain/application layer.
- Use consistent error responses and appropriate HTTP status codes.
- Never return stack traces, SQL details, internal exception text, credentials, tokens, or secrets to clients.
- Treat uploaded files, URLs, webhook bodies, and user-controlled search parameters as untrusted input.

## Secrets and configuration

- Do not commit credentials, signing keys, API keys, provider secrets, or production connection strings.
- Use environment variables or a suitable secret manager for sensitive deployment configuration.
- Do not provide insecure hardcoded fallback secrets.
- Keep dev/test configuration separate from production configuration.
- Log configuration state safely; never log secret values.

## Payment feature flag

A flag such as `ONLINE_PAYMENT_ENABLED` is a guard, not a payment integration.

- If disabled, real online payment initiation must fail closed or be unavailable according to a documented API contract.
- Mock payment must be clearly identified and only enabled in explicitly permitted environments/configurations.
- Production must not silently fall back to mock payment.
- Enabling the flag must not imply a real provider is configured. Validate provider configuration and fail startup or fail the payment operation safely when required configuration is missing.
- Verify provider webhooks and use idempotent state transitions.

## Logging and privacy

- Never log passwords, raw JWTs, refresh tokens, reset tokens, payment secrets, or sensitive personal data.
- Use request/correlation IDs without embedding personal data in them.
- Keep security-relevant audit events useful but privacy-conscious.
- Avoid logging full payment payloads or full authentication request bodies.
