# Identity Module Boundaries

## Current migration status

Status: `INVENTORIED` (target Kotlin repository only).

The checked-in Kotlin repository currently contains only the Spring Boot application entry point and a small base-package test. It has no authentication or user-management controllers, use cases, entities, repositories, migrations, API contracts, or feature tests. The legacy NestJS/Prisma source and its database schema are not present in this repository. As a result, this document records the proposed ownership boundary; it does not claim that identity behavior has been migrated or verified.

| Concern | Proposed owner | Current Kotlin implementation | Evidence still required before migration |
|---|---|---|---|
| Core account, profile, status, and role assignments | `identity.users` | None | Legacy modules, guards, DTOs, Prisma models, and role-management rules |
| Password hash and password-hashing policy | One owner inside `identity` (prefer `identity.auth`) | None | Legacy hash format, registration/login/password-change flows, and safe hash-upgrade requirements |
| Registration and login orchestration | `identity.auth` | None | Legacy controllers, services, validation, status codes, response DTOs, and tests |
| Access-token issuance and validation integration | `identity.auth` | None | Signing/verification configuration, claims, expiry, issuer/audience, guards, and consumers |
| Refresh-token/session records and lifecycle | `identity.auth` | None | Storage schema, rotation, replay handling, logout semantics, expiry, and concurrency behavior |
| Password-reset and email-verification tokens | `identity.auth` | None | Token schema, delivery workflow, expiry, single-use behavior, and enumeration protections |

## Dependency rule

`auth` may call a narrow public `users.application` use case to create or retrieve an account. `auth` must not import a `users` persistence entity or repository. `users` must not implement login, token issuance/rotation, or session revocation. `identity.shared` is reserved for a concrete stable contract used by both modules; it is not an owner for entities or a generic dumping ground. This proposed direction avoids a cycle: `auth -> users.application`, with no reverse dependency.

Do not create the package tree until its first real implementation is ready. Place HTTP endpoints and DTOs in each module's `api`, use cases in `application`, rules in `domain`, and database/provider adapters in `infrastructure` where those boundaries provide value.

## Compatibility, data, and verification

No identity endpoint or persistence record currently exists in the target source tree, and no legacy source/schema was available for comparison. No route, status code, DTO, database object, or authentication behavior was changed in this inventory. No Flyway migration is proposed.

Before moving an identity feature to `IMPLEMENTED`, inspect the legacy controllers, DTOs, guards, interceptors, services, Prisma schema and migrations, and relevant tests. Record the route and response contract, account/credential/token ownership, authorization, transaction boundary, token lifecycle, and safe legacy hash compatibility. Add behavior tests for registration, login, password changes, authorization, and session invalidation as those use cases are implemented. Verify dependency direction and persistence encapsulation from actual code. Only mark it `VERIFIED` after the agreed checks and compatibility tests have run.

The password-hash owner above is a proposal, not a confirmed legacy fact. Resolve it during the legacy audit before implementing credential storage; keep exactly one owner either way.
