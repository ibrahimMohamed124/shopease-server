# ShopEase Migration Blueprint

> Copy this file to `docs/migration/MIGRATION_BLUEPRINT.md` and fill it using evidence from both repositories. Do not invent unknown behavior. Mark uncertain items `UNKNOWN` and state what must be inspected.

## 1. Repository and runtime inventory

- Legacy repository/branch:
- NestJS version:
- Node.js version:
- Prisma version:
- PostgreSQL version:
- New repository/branch:
- Spring Boot version:
- Kotlin version:
- JDK version:
- Gradle version:
- Local test/build commands:
- Current baseline test result:

Do not include secret values or connection strings.

## 2. Feature inventory

| Feature | Legacy files/evidence | Current behavior | Target module | Status | Risks / unknowns |
|---|---|---|---|---|---|
| Identity/Auth | | | `identity` | `NOT_STARTED` | |
| Catalog/Search | | | `catalog` | `NOT_STARTED` | |
| Cart | | | `cart` | `NOT_STARTED` | |
| Wishlist | | | `wishlist` | `NOT_STARTED` | |
| Checkout | | | `checkout` | `NOT_STARTED` | |
| Orders | | | `orders` | `NOT_STARTED` | |
| Payments/Webhooks | | | `payments` | `NOT_STARTED` | |
| Inventory | | | `inventory` | `NOT_STARTED` | |
| Returns/Refunds | | | `returns` | `NOT_STARTED` | |
| Reviews | | | `reviews` | `NOT_STARTED` | |
| Customers/Addresses | | | `customers` / `addresses` | `NOT_STARTED` | |
| Admin/Reports | | | `admin` | `NOT_STARTED` | |
| Notifications | | | `notifications` | `NOT_STARTED` | |

Adjust the table based on actual code; do not assume all rows exist.

## 3. API contract inventory

For each route, record:
- method and path;
- authentication and authorization;
- request DTO and validation;
- success status and response shape;
- error status and response shape;
- pagination/filter/sort behavior;
- idempotency/retry behavior;
- legacy source file and test evidence;
- target controller/use case;
- compatibility test.

## 4. Database inventory

- Tables and relationships:
- Prisma models:
- Migration history:
- Enums:
- Unique/check/foreign-key constraints:
- Indexes, including PostgreSQL-specific indexes:
- Extensions:
- Triggers/functions:
- Timestamp/time-zone assumptions:
- Monetary precision and rounding:
- Seed data behavior:
- Existing Flyway history, if any:
- Proposed Flyway baseline strategy:
- Backup/restore verification:

## 5. Security inventory

- Password hash format and upgrade plan:
- Access-token format/expiry/claims:
- Refresh-token storage/rotation/revocation:
- Password reset behavior:
- Role and object-ownership rules:
- CORS/CSRF policy:
- Rate limiting:
- Admin endpoint inventory:
- Webhook authenticity verification:
- Secrets/configuration gaps:

## 6. Critical business invariants

List evidence-backed rules for:
- product pricing and availability;
- cart quantity and duplicate behavior;
- checkout total calculation;
- order creation and item snapshots;
- inventory consistency;
- idempotency;
- payment state transitions;
- refund/return eligibility;
- review eligibility;
- customer data ownership;
- admin reporting.

For each rule, include legacy evidence and a test case.

## 7. Dependency graph and migration order

List feature dependencies and explain the selected order. Avoid assuming the default playbook order is correct without inspecting actual dependencies.

## 8. Compatibility and regression test plan

For every critical endpoint and use case, list:
- success case;
- invalid input;
- unauthenticated/unauthorized case;
- not-found case;
- duplicate/retry case;
- concurrency/failure case where relevant;
- expected status and response;
- test location.

## 9. Risks and unresolved questions

| Question/risk | Evidence found | Impact | Required action | Owner/status |
|---|---|---|---|---|

## 10. Intentional deviations

| Legacy behavior | Proposed new behavior | Reason | API/data impact | Approval | Tests |
|---|---|---|---|---|---|

## 11. Per-feature progress

| Feature | Status | Tests passing | Contract parity | DB impact | Remaining work |
|---|---|---|---|---|---|

## 12. Cutover and rollback plan

- Backup and restore test:
- Migration rehearsal:
- Traffic/write freeze:
- Data verification:
- New application health checks:
- Rollback trigger:
- Rollback steps:
- Data compatibility during rollback:
- Owner approval:
