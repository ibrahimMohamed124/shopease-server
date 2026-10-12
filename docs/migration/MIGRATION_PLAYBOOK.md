# NestJS + Prisma to Spring Boot + Kotlin Migration Playbook

## Objective

Reimplement ShopEase on Spring Boot/Kotlin with the same required business behavior and compatible API contracts, while using Spring-native architecture and avoiding a mechanical translation.

## Phase 0 — Freeze the baseline

1. Identify the exact legacy NestJS repository, active branch, runtime version, Prisma version, database URL target (never print secrets), migrations, environment configuration, and test commands.
2. Identify the new Kotlin repository and its actual package/dependency versions.
3. Record the current database schema and migration history.
4. Run the legacy test suite and record its baseline.
5. Capture route list, request/response examples, status codes, pagination, filters, error shape, auth guards, interceptors, and validation behavior.
6. List features as implemented, partial, absent, or unclear.
7. Do not change production data or enable new schema migrations during the audit.

Deliverable: `docs/migration/MIGRATION_BLUEPRINT.md`, based on the provided template.

## Phase 1 — Establish the foundation

- Confirm stable compatible Spring Boot/Kotlin/JDK/Gradle versions.
- Configure profiles and externalized configuration without secrets in source control.
- Configure PostgreSQL connectivity with safe read-only validation first.
- Configure Flyway adoption/baseline strategy before applying migrations.
- Set Hibernate schema mode to `validate`.
- Establish global error handling, request IDs, validation, logging, security foundations, and test infrastructure.
- Add a health check and a reproducible local development workflow.

Do not implement every feature before the foundation is tested.

## Phase 2 — Migrate in dependency order

Use this default order only after validating actual dependencies:
1. shared API conventions and test infrastructure;
2. identity/authentication/authorization;
3. catalog/categories/search;
4. addresses/customer profile as required;
5. cart and wishlist;
6. checkout and order creation;
7. inventory consistency/reservation;
8. payment abstraction and webhook handling;
9. order lifecycle, cancellations, returns/refunds;
10. reviews;
11. admin/reporting;
12. notifications and secondary integrations.

A different order is acceptable if the dependency graph and rationale are documented.

## Phase 3 — Migrate one vertical slice at a time

For each feature:
1. Read all relevant legacy controllers, DTOs, services, repositories, guards, pipes/interceptors, Prisma schema usage, migrations, and tests.
2. Write a short behavior specification and list the API contracts.
3. Identify ownership, data model, invariants, transaction boundary, security rules, concurrency/idempotency behavior, and external side effects.
4. Implement the feature in the target module with Spring-native patterns.
5. Add unit, integration, API contract, and regression tests.
6. Compare legacy and new outputs for equivalent cases.
7. Run the relevant build/test checks.
8. Update the blueprint with status, decisions, deviations, and risks.
9. Only then proceed to the next feature.

Do not create stubs that return fake success merely to satisfy the frontend. If a feature is not implemented, return a truthful error and record it as incomplete.

## Phase 4 — API compatibility

Preserve by default:
- route and HTTP method;
- auth requirement and role/ownership checks;
- request field names and validation semantics;
- response shape and serialization;
- status codes and error response shape;
- pagination, filtering, sorting, and search semantics;
- idempotency and retry semantics.

Intentional breaking changes require a documented reason, tests, and an approved rollout plan.

## Phase 5 — Data compatibility

- Use the same PostgreSQL database only when the migration plan explicitly permits it.
- Do not let both applications write conflicting representations without a defined ownership/transition strategy.
- Test migrations against a disposable database copy.
- Confirm rollback/restore procedures before cutover.
- If schema changes must support both old and new versions temporarily, use an expand-and-contract strategy.
- Validate row counts and key business invariants before and after cutover; never rely only on the application starting.

## Phase 6 — Cutover readiness

Required before production traffic moves:
- critical API contract tests pass;
- authorization tests pass;
- payment and order failure/retry paths pass;
- database migration and restore procedure tested;
- secrets/configuration reviewed;
- logs/health/metrics are usable;
- no unintended mock-payment fallback;
- deployment and rollback plan documented;
- known gaps explicitly accepted by the project owner.

Prefer a planned cutover with a defined write-freeze or traffic-routing strategy unless a safe gradual migration has been designed. Do not run both systems as independent writers to the same records without a clear strategy.

## Migration status labels

Use:
- `NOT_STARTED`
- `INVENTORIED`
- `IN_PROGRESS`
- `IMPLEMENTED`
- `VERIFIED`
- `BLOCKED`
- `INTENTIONAL_DEVIATION`

`IMPLEMENTED` is not the same as `VERIFIED`. Only mark a feature `VERIFIED` after relevant tests and compatibility checks pass.

## Decision log

For every material deviation, record:
- date;
- decision;
- alternatives considered;
- reason;
- API/database compatibility impact;
- tests and migration/rollback requirements.
