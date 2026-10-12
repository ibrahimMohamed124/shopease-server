# API Compatibility Rules

## Default policy

The Kotlin API must preserve the legacy NestJS API contract unless an intentional change is documented and approved.

## What to compare

For each endpoint, compare:
- HTTP method and route;
- path/query/body parameter names and types;
- required/optional/null semantics;
- validation rules and validation error behavior;
- authentication, roles, and object ownership;
- success status code;
- response JSON field names, nesting, null handling, and date/decimal serialization;
- error status and error response structure;
- pagination defaults and metadata;
- sorting/filtering/search semantics;
- idempotency and retry behavior.

## DTO and serialization rules

- Use explicit request and response DTOs.
- Do not expose JPA entities.
- Avoid changing field names to Kotlin naming preferences if the public API uses different names.
- Be deliberate with Kotlin nullable types and Jackson configuration.
- Verify enum serialization and date/time formats.
- Verify `BigDecimal` serialization and decimal precision.
- Do not return internal stack traces or exception messages.

## Compatibility tests

Maintain a route/contract inventory and add tests for representative success and failure cases. For critical endpoints, compare JSON semantics against the legacy application using equivalent database state.

Do not change a response merely because the new framework's default serialization differs. Configure the new implementation to preserve the contract or document an approved API version/change.

## Breaking changes

A breaking change requires:
1. the reason and affected consumers;
2. the old and new contract;
3. tests;
4. data/client migration needs;
5. rollout and rollback plan;
6. explicit approval.
