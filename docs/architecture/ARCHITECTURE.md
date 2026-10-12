# ShopEase Backend Architecture

## Decision summary

ShopEase is a Kotlin + Spring Boot **modular monolith**. It is not a mechanical NestJS rewrite and is not split into microservices at this stage.

- Base package: `com.shopease.server`
- HTTP stack: Spring MVC
- Persistence: Spring Data JPA/Hibernate where appropriate, with explicit SQL/custom repositories when required
- Database: PostgreSQL
- Schema management: Flyway
- Security: Spring Security
- Build: Gradle Kotlin DSL
- API contract: preserve the legacy contract unless a change is approved and documented

Use a stable Spring Boot release compatible with the project's Kotlin/JDK and dependency versions. Do not introduce snapshot/milestone versions into the production baseline without explicit approval.

## Package-by-feature structure

The current Kotlin source is still a project skeleton. Its application entry point uses the documented `com.shopease.server` base package. No feature module has been implemented yet; the tree below describes target locations and must not be read as evidence that those features already exist.

Illustrative structure; create only packages needed by actual features:

```text
com.shopease.server
├── ShopEaseApplication.kt
├── shared
│   ├── config
│   ├── exception
│   ├── security
│   ├── web
│   └── observability
├── identity
│   ├── auth
│   ├── users
│   └── shared
├── catalog
├── cart
├── wishlist
├── checkout
├── orders
├── payments
├── inventory
├── reviews
├── returns
├── addresses
├── customers
├── admin
└── notifications
```

Each feature module may use these internal areas when useful:

```text
<feature>/
├── api/                 # Controllers, request/response DTOs, API mapping
├── application/         # Use cases, commands, orchestration, transaction boundary
├── domain/              # Business rules, invariants, domain types/interfaces
└── infrastructure/      # JPA mappings, repository adapters, provider integrations
```

This is a guide, not a mandate to create empty folders or four layers for every trivial feature. Keep simple features simple. Create identity subpackages with their first real implementation rather than committing empty package placeholders. See [IDENTITY_MODULE_BOUNDARIES.md](../migration/IDENTITY_MODULE_BOUNDARIES.md) for current ownership and migration notes.

## Dependency direction

Preferred dependency direction:

```text
api -> application -> domain
infrastructure -> domain (implements persistence/provider ports)
application -> infrastructure only through deliberate Spring wiring or ports
```

The API layer must not contain business rules. The domain must not depend on Spring MVC, HTTP DTOs, controllers, or database implementation details. Infrastructure must not become the owner of business policy.

Application use cases coordinate work and define transaction boundaries. Domain objects/services enforce business invariants. Repositories persist and retrieve data; they should not hide major business workflows.

## Module boundaries

- A module owns its business vocabulary, rules, use cases, API, and persistence details.
- Other modules must not query its tables through its internal repository or manipulate its entities directly.
- Cross-module interaction should use a public application service/use case or a stable domain-level contract.
- Avoid circular module dependencies. If two modules need each other, reconsider ownership or introduce a narrow event/contract.
- Do not use `shared` as a shortcut for code that belongs to one feature.
- Shared utilities must be generic, stable, and used by multiple modules.

## DDD: practical, not ceremonial

Use DDD concepts when they clarify business behavior:
- entities and value objects for meaningful identity/constraints;
- aggregates where consistency boundaries matter;
- domain services for rules that do not naturally belong to one entity;
- domain events where they decouple meaningful business reactions.

Do not add a repository interface and adapter for every table by default. Do not build a full CQRS or event-sourcing system without an approved need. Prefer straightforward code over ceremony.

## Kotlin and Spring conventions

- Use `data class` for request/response DTOs and simple immutable data carriers.
- Do not automatically use `data class` for JPA entities. Carefully consider generated `equals`, `hashCode`, `copy`, lazy associations, and entity identity.
- Keep nullability explicit. Do not use `!!` to avoid designing null behavior.
- Use `BigDecimal` for money and explicit rounding/currency rules.
- Use constructor injection; avoid field injection.
- Use Spring MVC for conventional REST endpoints. Do not adopt WebFlux or coroutines everywhere by default.
- Use Bean Validation at API boundaries, and enforce business invariants in the domain/application layer as appropriate.
- Never serialize JPA entities directly as public API responses.

## Transactions and external side effects

- Put transaction boundaries around application use cases that need atomic database changes.
- Understand Spring proxy/self-invocation behavior before relying on `@Transactional`.
- External calls (payment providers, email, shipping, etc.) are not made atomic by a database transaction.
- For reliable post-commit actions, evaluate an outbox, retry-safe processing, and reconciliation.
- Use events only when they improve decoupling or represent a meaningful fact; do not introduce an event bus for every method call.

## Explicit non-goals for the initial migration

Do not introduce microservices, Kafka, full CQRS, event sourcing, WebFlux, or a distributed cache strategy without a written reason and explicit approval. Start with a modular monolith and extract services only when operational or organizational evidence justifies it.
