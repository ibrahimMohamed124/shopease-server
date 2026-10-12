# ShopEase Backend — AI Agent Rules

This file is mandatory for every AI coding agent working in this repository. Read it before inspecting or changing code. The rules apply to all tasks unless the user explicitly overrides a specific rule.

## 1. Mission and non-negotiable principles

- Target project: migrate the existing ShopEase backend from NestJS + Prisma to Kotlin + Spring Boot.
- Target architecture: **modular monolith**, organized by business feature, with explicit module boundaries and practical domain modeling.
- This is a **behavior-preserving redesign**, not a line-by-line translation. Preserve required business behavior and API compatibility unless a deliberate, documented change is approved.
- Prefer idiomatic, maintainable Spring Boot and Kotlin over imitating NestJS patterns.
- Do not invent business rules. If behavior is unclear, inspect the legacy implementation, tests, schema, migrations, and API usage. Record unresolved ambiguity instead of guessing.
- Keep changes focused. Do not perform unrelated refactors, dependency upgrades, formatting sweeps, or API redesigns during a feature migration.
- Never claim a feature is complete without running the relevant checks or clearly stating what could not be run.

## 2. Mandatory reading order

Before implementing a migration feature, read:
1. `AGENTS.md`
2. `docs/architecture/ARCHITECTURE.md`
3. `docs/architecture/DOMAIN_BOUNDARIES.md`
4. `docs/architecture/DATABASE_AND_MIGRATIONS.md`
5. `docs/architecture/SECURITY.md`
6. `docs/quality/TESTING.md`
7. `docs/migration/MIGRATION_PLAYBOOK.md`
8. `docs/quality/KOTLIN_SPRING_GUIDELINES.md`
9. `docs/quality/GIT_WORKFLOW.md`
10. `docs/quality/API_COMPATIBILITY.md`
11. `docs/quality/AI_AGENT_WORKFLOW.md`
12. The relevant feature/module docs and current migration blueprint, if present.

If a document is missing, do not silently assume its requirements; report the gap and follow the principles in this file.

## 3. Mandatory workflow for every task

1. **Inspect first.** Read the relevant Kotlin source, legacy NestJS/Prisma source when available, schema, migrations, tests, and API contracts.
2. **Summarize the plan.** Identify affected modules, behavior to preserve, database impact, security impact, and tests to add/update.
3. **Establish the baseline.** Run relevant existing tests/build or explain why not possible.
4. **Create a dedicated feature branch before editing**, following `docs/quality/GIT_WORKFLOW.md`. Inspect and preserve the working tree first. Do not work directly on the default branch.
5. **Implement the smallest coherent change.** Keep module ownership clear and avoid unnecessary abstractions.
6. **Test.** Add/update unit, integration, API, and regression tests appropriate to the change.
7. **Review.** Check authorization, transaction boundaries, concurrency, idempotency, data compatibility, logs, and error responses.
8. **Validate.** Run the build, tests, formatting/lint checks that the repository actually configures.
9. **Review and commit.** Inspect the entire diff, check for secrets/unrelated files, and use Conventional Commits. Never include LLM/AI/tool names in branch names, commit metadata/messages, PR titles/descriptions, issue text, tags, or release notes. Do not falsify Git author identity.
10. **Push and open a PR.** Push the finished feature branch to GitHub and open a suitable PR for that feature. Verify the branch and PR URL/status. Do not merge unless explicitly authorized. If GitHub credentials/permissions/tools are unavailable, report the exact blocker and never claim the work was pushed or a PR opened.
11. **Report honestly.** List files changed, behavior changed, exact commands and results, branch/commit/verified PR link, remaining risks, and any manual steps.

Do not begin by rewriting an entire module without first mapping its behavior and contracts.

## 4. Architecture rules

- Use a **feature-first modular monolith** rooted at `com.shopease.server`.
- Modules own their business rules, application use cases, API endpoints, and persistence adapters.
- Avoid global technical folders that become dumping grounds for all controllers/services/repositories.
- Cross-module calls must use a clear public application/domain interface; do not reach into another module's internal repository or persistence entity.
- `shared` is for genuinely cross-cutting, stable capabilities only (security primitives, error handling, configuration, observability, shared web conventions). Do not place feature-specific logic there.
- Keep HTTP concerns in `api`; orchestration and use cases in `application`; business invariants in `domain`; database/provider implementations in `infrastructure`.
- Do not create an interface, mapper, factory, wrapper, or layer solely to make the design look enterprise-grade. Add abstractions when they protect a boundary, enable testing, or isolate a meaningful external dependency.
- Do not introduce microservices, Kafka, full CQRS, event sourcing, or WebFlux unless explicitly justified and approved.
- Use Spring MVC for ordinary REST APIs unless a concrete requirement supports another model.
- Prefer explicit, understandable Kotlin. Avoid clever DSLs, unnecessary reflection, excessive extension functions, and coroutine/reactive complexity without a real need.

## 5. Database and persistence safety

- PostgreSQL is the system of record.
- Flyway owns schema evolution. Hibernate must use `ddl-auto: validate` (or the equivalent approved configuration); never use `update`, `create`, or `create-drop` against the shared application database.
- Never modify an existing migration that may already have run in any environment. Add a new migration instead.
- Before writing a migration, inspect the live schema, Prisma schema, legacy migration history, indexes, constraints, triggers, enum definitions, and data assumptions.
- Do not reset, drop, truncate, reseed, or destructively alter user data unless the user explicitly authorizes it and a recovery plan exists.
- Do not assume activating Flyway against a legacy database is safe. Determine the baseline strategy and validate it on a disposable copy first.
- Preserve identifiers, foreign keys, uniqueness, nullability, precision, time semantics, and relevant indexes unless a reviewed migration intentionally changes them.
- Use `BigDecimal` for monetary amounts. Never use `Double` or `Float` for money.
- Avoid accidental N+1 queries, unbounded collection loading, and serialization of JPA entities directly to API responses.
- Use explicit query strategies for search/reporting where JPA is not a good fit; preserve and verify PostgreSQL-specific features such as `pg_trgm` and GIN indexes where relevant.

## 6. Transaction, concurrency, and idempotency rules

- Define transactions around business use cases, not arbitrarily around every method.
- Do not assume `@Transactional` alone solves concurrency problems.
- For stock, checkout, payment state, returns, and other contested resources, inspect race conditions and use appropriate conditional updates, constraints, locking, or versioning.
- Idempotency is required wherever retries can cause duplicate orders, charges, refunds, inventory changes, or other irreversible side effects.
- Payment and external provider calls cannot be made atomic with a local PostgreSQL transaction. Design failure handling explicitly; use an outbox or reconciliation strategy when justified.
- Never mark an order paid merely because a client says payment succeeded. Validate the provider result/webhook according to the actual integration design.
- Event handlers and webhooks must tolerate duplicate delivery where applicable.

## 7. Security rules

- Enforce authorization on the server for every protected operation; frontend visibility is not authorization.
- Follow `docs/architecture/SECURITY.md`.
- Never log passwords, access/refresh tokens, reset tokens, payment secrets, or sensitive personal data.
- Store passwords using a suitable adaptive password hash and preserve a safe migration path for legacy hashes.
- Validate token issuer/audience/expiry/signature and refresh-token lifecycle according to the chosen design.
- Do not weaken CORS, CSRF, endpoint authorization, validation, or password policy merely to make tests pass.
- Do not add hardcoded credentials, production secrets, or insecure fallback secrets.
- Keep mock payment implementations impossible to confuse with real providers in production. A boolean feature flag alone does not constitute a real payment integration.

## 8. API and compatibility rules

- Preserve current routes, HTTP methods, status codes, response shapes, pagination semantics, sorting/filtering behavior, and error contracts unless a change is explicitly approved.
- Compare the legacy NestJS controllers, DTOs, guards, interceptors, pipes, serializers, and exception filters; their behavior may not be obvious from service code alone.
- Keep request/response DTOs separate from persistence entities.
- Use Bean Validation for input shape and domain logic for business invariants.
- Do not expose internal exception messages, stack traces, database details, or secrets to clients.
- Document any intentional API-breaking change and provide migration notes.

## 9. Testing and completion gate

A feature is not complete until:
- Relevant tests exist for its business rules and edge cases.
- Database behavior is tested against PostgreSQL for features relying on PostgreSQL-specific semantics.
- Authorization and validation paths are covered.
- Critical retries/concurrency/idempotency cases are covered where relevant.
- The application builds and the relevant tests pass, or the report explicitly explains the blocker.
- No unrelated tests were deleted or weakened to obtain a green build.

## 10. Prohibited shortcuts

Never:
- translate NestJS files mechanically into Kotlin files;
- recreate a global `services/` or `repositories/` dumping ground;
- use Hibernate schema auto-update;
- change the database schema without a reviewed migration;
- replace meaningful tests with mocks that bypass the behavior under test;
- silently remove a feature because it is inconvenient;
- change API behavior without documenting it;
- add dependencies without explaining why they are needed;
- swallow exceptions or return success after a failed operation;
- declare success based only on compilation;
- mix unrelated features into a branch/PR;
- push feature work directly to the default/protected branch;
- claim a push or PR succeeded without verifying it;
- mention LLM/AI assistant/model/tool names in branches, commits, PRs, issues, tags, or release metadata;
- merge a PR or enable auto-merge without authorization.

## 11. Required final response format

For each coding task, report:
1. **Implemented:** concise description.
2. **Files changed:** list.
3. **Behavior/contracts:** compatibility preserved or intentional differences.
4. **Validation:** exact commands run and pass/fail outcomes.
5. **Database impact:** migration name and risk, or “none”.
6. **Security/concurrency notes:** relevant findings.
7. **Remaining work/risks:** clearly stated.

If asked only to analyze or plan, do not modify files; return findings, evidence, assumptions, and a phased plan.
