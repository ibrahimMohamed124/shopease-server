# ShopEase Domain Boundaries

This document defines initial ownership hypotheses. Validate them against the legacy code and database before finalizing. Do not assume the names alone fully describe the existing behavior.

## Initial module map

The target application currently has no implemented feature modules. For the identity ownership inventory and evidence still needed from the legacy system, see [Identity Module Boundaries](../migration/IDENTITY_MODULE_BOUNDARIES.md). Treat this table as an ownership hypothesis until that legacy audit is complete.

| Module | Owns | Must not own |
|---|---|---|
| `identity` | user identity, credentials, login, refresh tokens, password reset, authentication lifecycle | product/order business rules |
| `catalog` | product data, categories, product details, search and catalog queries | order placement or payment state |
| `cart` | active cart and cart-item operations | order lifecycle after checkout |
| `wishlist` | saved products and wishlist operations | inventory reservations |
| `checkout` | orchestration and validation to place an order | provider-specific payment implementation |
| `orders` | order records, item snapshots, order state transitions, order queries | raw payment-provider credentials |
| `inventory` | stock availability and reservation/adjustment rules, if inventory is modeled explicitly | catalog display formatting |
| `payments` | payment attempts, provider abstraction, provider responses, webhook processing, payment idempotency | unverified client claims of successful payment |
| `returns` | return eligibility, return lifecycle, refund coordination | general payment provider implementation |
| `reviews` | review submission/modification and verified-purchase eligibility | order internals beyond a public query/contract |
| `customers` | customer profile and customer-facing preferences if distinct from identity | credentials and token internals |
| `addresses` | address ownership and address validation/selection | checkout orchestration |
| `admin` | administrative use cases and reporting projections | bypassing domain authorization |
| `notifications` | email/push/message delivery and templates | authoritative order/payment state |

These boundaries may be adjusted after the legacy audit. Document why any change is made.

## Critical business flows

### Checkout / order creation

1. Authenticate and authorize the caller.
2. Validate the idempotency key and request.
3. Load/validate the cart and current product/price/availability information.
4. Apply the actual legacy business rules for shipping, discounts, taxes, and totals; do not invent rules.
5. Create an order with immutable item/price snapshots as appropriate.
6. Reserve/decrement inventory according to the agreed inventory model and concurrency strategy.
7. Persist the required state changes in a well-defined transaction.
8. Trigger external payment work using a failure-safe design.
9. Clear or transition the cart only when the agreed point in the flow has been reached.
10. Ensure retries cannot create duplicate orders or duplicate irreversible effects.

The exact order of payment initiation and inventory reservation depends on the existing design and provider semantics. Inspect and document it; do not blindly copy the sequence above if it conflicts with established behavior.

### Payment

- Distinguish order state from payment-attempt state.
- A mock provider must be explicit and isolated from real provider implementations.
- Treat provider webhooks as untrusted input until signature/authenticity and relevant transaction fields are verified.
- Make webhook processing idempotent.
- Verify order reference, currency, amount, provider transaction ID, and expected state according to the actual provider contract.
- Never trust a browser redirect alone as proof of payment.
- Do not mark an order paid on an arbitrary client request.

### Orders and state transitions

- Define allowed transitions explicitly based on the existing product rules.
- Reject illegal transitions; do not let controllers set arbitrary status strings.
- Record timestamps and audit-relevant facts consistently.
- Handle concurrent updates deliberately.
- Refund, cancellation, fulfillment, and return operations must be coordinated without assuming they are one database-only action.

### Wishlist and cart

- Use uniqueness constraints or equivalent protection where duplicate entries are forbidden.
- Determine whether the legacy API is idempotent for add/remove operations and preserve that behavior.
- For cart pricing, define whether displayed prices are snapshots or recalculated at checkout based on the current business rules.
- Validate quantities and product availability.

### Reviews and returns

- Preserve the actual legacy eligibility conditions, such as verified delivery, ownership, duplicate review policy, return window, and refund limits.
- Do not infer eligibility only from frontend behavior.
- Enforce important constraints in the database when appropriate in addition to application checks.

## Cross-module interaction

Use explicit application contracts for synchronous requests. Use domain/application events for meaningful asynchronous reactions where appropriate.

A module must not:
- inject another module's internal JPA repository;
- modify another module's persistence entity directly;
- expose JPA entities as public cross-module contracts;
- import internal implementation packages from another module.

## Before implementing a feature

Document:
- owner module;
- input/output contract;
- business invariants;
- transaction boundary;
- data ownership;
- authorization policy;
- retry/idempotency behavior;
- failure and recovery behavior;
- tests required.
