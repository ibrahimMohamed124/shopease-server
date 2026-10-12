# Testing and Quality Gates

## Test pyramid

Use a balanced set of:
- unit tests for domain rules and pure logic;
- application/use-case tests for orchestration and transaction-sensitive behavior;
- Spring integration tests for wiring, validation, security, and persistence;
- API contract tests for route/status/response compatibility;
- PostgreSQL integration tests for actual SQL, constraints, indexes, locking, and PostgreSQL-specific behavior;
- end-to-end tests for a small number of critical customer journeys.

Use Testcontainers with PostgreSQL for tests whose correctness depends on PostgreSQL. H2 is not a substitute for `pg_trgm`, GIN, PostgreSQL constraints, or production-like locking semantics.

## Migration parity

For each legacy feature, compare old and new behavior using equivalent input and database state. Cover:
- successful requests;
- validation failures;
- unauthenticated and unauthorized requests;
- missing resources;
- duplicate requests;
- pagination/filtering/sorting;
- transaction rollback;
- concurrent writes where relevant;
- external provider failure and retry;
- response and error shape.

Do not copy bugs blindly. If the old behavior appears defective, document the evidence and ask for/obtain approval before intentionally changing the contract.

## Critical scenarios

### Authentication
- valid/invalid credentials;
- expired, malformed, revoked, and wrong-type tokens;
- refresh-token rotation/reuse behavior;
- user ownership and role enforcement;
- password-reset token expiry and single-use behavior.

### Catalog
- filtering, sorting, pagination;
- search partial-match semantics;
- empty result behavior;
- PostgreSQL-specific search/index behavior where used.

### Cart and wishlist
- invalid quantity and missing product;
- duplicate add semantics;
- ownership checks;
- concurrent updates and uniqueness constraints.

### Checkout, orders, inventory
- same idempotency key and same payload;
- same key with a different payload;
- stock contention;
- failed operation rolls back local changes correctly;
- legal and illegal state transitions;
- retry after partial failure.

### Payments and returns
- duplicate webhooks;
- invalid signature or mismatched transaction details;
- provider timeout and retry;
- duplicate refund/return request;
- payment failure does not falsely mark an order paid;
- mock provider cannot be used accidentally in production.

### Reviews and admin
- verified-purchase eligibility;
- duplicate review policy;
- object-level authorization;
- admin-only endpoints and report query correctness.

## Build and checks

Use the repository's actual Gradle wrapper and configured tasks. Common checks may include:

```bash
./gradlew clean test
./gradlew build
```

On Windows PowerShell:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Do not claim checks passed unless they were executed and their results were observed. If the environment prevents a check, report the exact blocker.

## Test quality rules

- Tests must assert behavior, not merely that a method was called.
- Do not mock away the core behavior under test.
- Do not delete, skip, or weaken tests solely to make the build green.
- Keep test data deterministic and isolated.
- Avoid tests that depend on execution order or external internet services.
