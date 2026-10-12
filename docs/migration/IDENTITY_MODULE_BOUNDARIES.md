# Identity Module Boundaries

## Status and evidence

Status: `INVENTORIED` (legacy behavior inspected; Kotlin identity feature not implemented).

The Kotlin repository started as an application skeleton with no identity feature code. The ignored local NestJS project is available for source comparison. Its guidance says not to run the app or tests unless asked; this migration therefore uses source, API inventory, Prisma schema, and migration files only. The local source tree is excluded from this repository and must never be staged or pushed.

## Legacy-to-Kotlin responsibility map

| Legacy source | Current responsibility | Kotlin destination |
|---|---|---|
| `src/auth/auth.controller.ts`, `src/auth/dto/*` | Registration, login, refresh, logout, forgot/reset/change-password HTTP contracts and validation | `identity.auth.api` |
| `src/auth/auth.service.ts` | Credential checks, bcrypt policy, token issuance, reset workflow, session orchestration | `identity.auth.application` (split by use case where helpful) |
| `src/auth/strategies/local.strategy.ts` | Email/password authentication adapter | `identity.auth.infrastructure` / Spring Security authentication provider |
| `src/auth/strategies/jwt.strategy.ts`, `jwt-refresh.strategy.ts`, `src/auth/guards/*` | Access/refresh token verification, session/activity checks, refresh replay handling | `identity.auth.infrastructure` / Spring Security token adapters and authorization configuration |
| `src/users/users.controller.ts`, `src/users/dto/*` | Authenticated profile update and avatar upload | `identity.users.api` |
| `src/users/users.service.ts` | Account lookup/create, profile rules, avatar metadata workflow; currently also bridges auth to persistence | `identity.users.application` for account/profile use cases; remove its auth/session responsibilities |
| `src/users/users.repository.ts` | Prisma user and session persistence, atomic session cap/rotation/password updates | `identity.users.infrastructure` for user persistence; session persistence moves to `identity.auth.infrastructure` behind an auth-owned port |
| `src/admin/controllers/admin-user.controller.ts`, `src/admin/services/admin-user.service.ts`, `src/admin/dto/*` | Admin account search/list/detail, role and status management | `identity.users.api` and `identity.users.application` (these endpoints manage accounts, not authentication) |
| `src/mail/mail.service.ts`, `src/mail/templates/password-reset.template.ts` | Password-reset delivery and template | `identity.auth.infrastructure` adapter using the shared notification/mail capability when available |

The current legacy dependency is mostly `auth -> UsersService`, but `UsersService` exports session and credential operations and the admin service reaches `UsersRepository` directly. In Kotlin, auth must depend only on a narrow public users application contract for account creation/lookup and credential verification/update. Neither module may import the other's infrastructure or persistence entity. Admin account use cases belong with users; the global admin module should delegate to them rather than own another user repository path.

## Ownership decisions

| Data or rule | Owner | Persistence / access boundary |
|---|---|---|
| Canonical account row, profile, avatar URL, account status, role | `identity.users` | One `users` record; no second auth user table or duplicate user aggregate |
| Password hash and bcrypt policy | `identity.auth` owns credential policy; `identity.users` owns the canonical account record | Existing `passwordHash` column stays on `users`; auth accesses it only through narrow users application credential operations |
| Password-reset token hash and expiry | `identity.auth` | Existing `resetTokenHash` and `resetTokenExpiresAt` columns remain on `users`; auth alone controls issue/consume/expiry and the users boundary persists them |
| Refresh sessions, current/previous token hashes, expiry | `identity.auth` | Existing `sessions` table; auth owns its entity, repository/port, rotation, replay handling, and revocation |
| Access/refresh JWT policy | `identity.auth` | Separate configured signing secrets and expiry policies; no token material in logs or API DTOs beyond the established token response |

This keeps one canonical user record while separating authentication policy and session lifecycle. It also establishes the one-way dependency `identity.auth -> identity.users.application`; users does not depend on auth. `identity.shared` stays unused until both modules need a concrete stable identity contract that belongs to neither one.

## Observed legacy API contracts

Routes and request validation are also inventoried in the ignored source's `docs/generated/routes.md`, `docs/API_CONVENTIONS.md`, controllers, and DTOs.

| Route | Access | Success contract |
|---|---|---|
| `POST /auth/register` | Public | `201`; `{ user: { id, name, email, role }, token, refreshToken }` |
| `POST /auth/login` | Public credentials | `200`; same shape as register |
| `POST /auth/refresh` | Refresh bearer token | `200`; `{ token, refreshToken }` |
| `POST /auth/logout` | Access bearer token | `200`; `{ message }`; revokes current session |
| `POST /auth/forgot-password` | Public | `200`; generic `{ message }` for known and unknown emails |
| `POST /auth/reset-password` | Public reset token | `200`; `{ message }`; invalid/expired token is rejected |
| `POST /auth/change-password` | Access bearer token | `200`; `{ message }`; all the user's sessions are revoked |
| `PATCH /users/me` | Access bearer token | `200`; direct `{ id, name, email, avatarUrl }` response |
| `POST /users/me/avatar` | Access bearer token, multipart field `avatar` | `200`; same direct profile response |
| `GET /admin/users` | Access bearer token, `ADMIN` | `200`; `{ users, page, limit, total, totalPages }` |
| `GET /admin/users/:id` | Access bearer token, `ADMIN` | `200`; `{ user }` |
| `PATCH /admin/users/:id/role` | Access bearer token, `ADMIN` | `200`; `{ user }`; cannot change own role |
| `PATCH /admin/users/:id/status` | Access bearer token, `ADMIN` | `200`; `{ user }`; cannot disable own account; disabling revokes sessions |

Other compatibility rules found in the source:

- Normalize email with trim + lowercase for registration, lookup, profile changes, and persisted values. Duplicate email maps to conflict.
- Registration name is 2–50 characters. Passwords are at least 8 characters and limited to 72 UTF-8 bytes for bcrypt. Registration does not accept a role; new accounts use the database `USER` default.
- Login performs a dummy bcrypt comparison for unknown emails. Disabled accounts are rejected only after a correct password, so login does not reveal their status.
- Access and refresh tokens use separate secrets; payload includes `sub`, `email`, and `sid`. Refresh tokens are SHA-256 hashed at rest, rotate conditionally, accept the previous hash for 30 seconds, and revoke the session on a signed replay outside that grace window. Access authentication checks session expiry and current account activity on each request.
- At most ten active sessions are kept per account. Session creation locks the user row, removes expired sessions, then evicts the oldest sessions before insert. Password changes and reset consumption update the password, clear reset state, and revoke every session in one database transaction.
- Reset tokens use 32 cryptographically random bytes; only the SHA-256 hash is stored, expiry is one hour, and responses do not reveal whether an email exists. Mail sending failure does not change that public response.
- Profile update permits only `name` and `email`; role/status/password are protected. Admin search is case-insensitive over name/email, page defaults to 1, limit defaults to 20 and caps at 100, results sort by `createdAt DESC, id DESC`, and `totalPages` is at least 1.
- Admin/user response DTOs omit password, reset-token, and refresh-token hashes. Avatar input is limited to 5 MiB, must be an actual JPEG/PNG/WEBP image (not a client-supplied MIME/extension claim), is re-encoded without metadata, and is constrained to 2048 pixels per dimension. Stored paths use a generated UUID and `/uploads/avatars/` prefix.
- Authentication throttles are 5/minute for register/login, 3/15 minutes for forgot-password per IP plus 5/hour per normalized email, 10/15 minutes for reset-password, and 5/minute for change-password. The Kotlin target currently has no throttling configuration; it must be designed before declaring API/security parity.

## Database compatibility

The legacy schema uses `users` with text UUID IDs, unique normalized email, `passwordHash`, nullable `avatarUrl`, `role` defaulting to `USER`, `isActive` defaulting to true, nullable reset-token hash/expiry, and millisecond timestamps. `sessions` uses text UUID IDs, a cascading user FK, token hash, nullable previous hash/grace expiry, mandatory expiry, and created/updated timestamps, with an index on `userId`.

Relevant legacy changes already present in the archive include `20261012090000_add_sessions_and_restrict_subcategory_delete`, `20261012120100_normalize_user_emails`, `20261012130000_session_expiry_and_remove_legacy_refresh_hash`, and `20261012140000_refresh_token_rotation_grace`. Do not copy or edit these Prisma migrations. Before adding or enabling Flyway, inspect the actual target database and choose/test a baseline strategy as required by the database guide. This architecture inventory proposes no schema migration.

## Kotlin implementation gate

The target Kotlin repository still has no identity controllers, use cases, entities, repositories, mail/image adapters, or feature tests. The root package has been aligned to `com.shopease.server`, but no identity behavior has been claimed as migrated. Before marking the feature `IMPLEMENTED`, create the real module packages, preserve the contracts above, and test registration/login, refresh rotation/reuse and concurrency, password changes/reset, authorization, user profile/admin behavior, bcrypt byte limits, persistence compatibility, and avatar validation/re-encoding. Test PostgreSQL-specific session updates against PostgreSQL. Mark `VERIFIED` only after the permitted checks actually run.
