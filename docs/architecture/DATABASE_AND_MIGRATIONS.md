# Database, JPA, and Flyway Rules

## Source of truth

PostgreSQL is the system of record. The legacy Prisma schema is a useful description, but the actual database and migration history must also be inspected. Do not assume Prisma schema alone captures every index, constraint, trigger, extension, or production-specific change.

## Flyway owns schema changes

- All new schema changes must be versioned Flyway migrations under `src/main/resources/db/migration/`.
- Hibernate schema mode must be `validate` in normal application configurations.
- Never use Hibernate `update`, `create`, or `create-drop` against a shared or valuable database.
- Never edit an applied Flyway migration; create a new versioned migration.
- Do not allow Hibernate, Flyway, ad-hoc SQL, and startup seed code to compete as schema owners.
- Test migrations on a disposable copy of the database before applying them to any valuable environment.

Example configuration:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

This example does not by itself make Flyway safe to enable against an existing legacy database.

## Existing database adoption

Before enabling Flyway against an existing database:
1. Identify the exact database/schema and migration history.
2. Compare the live schema with the expected baseline.
3. Decide whether a Flyway baseline is appropriate.
4. Set and review the baseline version and the first migration that will run.
5. Test startup and migration against a disposable clone.
6. Confirm that no migration will recreate existing tables or destroy data.
7. Document the adopted baseline and recovery procedure.

Never reset or recreate the database just to make the migration tool happy.

## Schema preservation checklist

For every table/column/index/constraint involved, verify:
- names and schema;
- data types and precision;
- nullable/default behavior;
- primary and foreign keys;
- unique constraints and check constraints;
- indexes and indexed column order;
- PostgreSQL extensions and operator classes;
- enum representation;
- timestamp/time-zone semantics;
- delete/update behavior;
- generated/default values;
- existing data compatibility.

Do not rename or alter identifiers merely to match Kotlin naming style. If a schema redesign is justified, plan an explicit compatible migration.

## JPA entity conventions

- Explicitly map table and column names when the physical schema differs from naming strategy defaults.
- Keep persistence entities internal to the owning module.
- Avoid returning entities from controllers.
- Avoid eager loading by default for every association.
- Avoid bidirectional relationships unless both directions are needed.
- Watch for N+1 queries and accidental large collection loads.
- Use projections or purpose-built queries for list screens and reports.
- Use optimistic locking (`@Version`) or another explicit concurrency strategy where it fits.
- Never use `Double`/`Float` for money; use `BigDecimal`.
- Define monetary scale, currency, and rounding based on actual product requirements.
- Be deliberate about Kotlin/JPA no-arg construction and proxy requirements; use the Kotlin compiler plugins/configuration compatible with the chosen Spring Boot/Kotlin versions.

## Queries and indexes

- Use Spring Data derived queries for simple, readable cases.
- Use JPQL, projections, Criteria, or custom repository implementations where justified.
- Use native SQL when PostgreSQL-specific features or measured performance require it.
- Preserve `pg_trgm`, GIN, and other relevant indexes only after confirming their actual definitions and query benefit.
- Inspect execution plans for important search, reporting, and pagination queries.
- Never fetch all rows and filter them in application memory when the query can be safely and efficiently executed by PostgreSQL.

## Data and monetary safety

- Use `BigDecimal` for money.
- Do not recalculate historical order totals from current product prices.
- Preserve order-item price/quantity snapshots when required by existing behavior.
- Never silently change rounding, tax, discount, shipping, or currency semantics.
- Avoid destructive data cleanup without an explicit approved plan and backup/recovery path.

## Seed data

Seed data must be clearly separated from production migrations and must never overwrite real data. Do not create production admin accounts or hardcoded credentials in migration scripts.
