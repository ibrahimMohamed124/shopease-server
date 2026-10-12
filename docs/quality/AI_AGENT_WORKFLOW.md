# AI Agent Workflow and Prompting Contract

This document supplements the root `AGENTS.md`. The root rules always win over a generated plan or assumptions made by an agent. For Kotlin/Spring implementation details, follow `KOTLIN_SPRING_GUIDELINES.md`; for all Git operations, follow `GIT_WORKFLOW.md`.

## Before editing

- Inspect the repository structure and Git status.
- Before editing, create a dedicated branch for the current feature/task per `GIT_WORKFLOW.md`, after preserving any pre-existing user changes.
- Read `AGENTS.md` and the relevant docs.
- Search for existing implementations before creating new ones.
- Inspect the legacy NestJS implementation and database evidence for migration tasks.
- Identify affected modules, API contracts, security rules, transactions, and tests.
- State a concise implementation plan before large or multi-file changes.

## During editing

- Make small, coherent changes.
- Follow current project naming and formatting conventions unless they violate documented architecture rules.
- Reuse established patterns only after verifying they are appropriate.
- Avoid unrelated changes and unnecessary dependencies.
- Never overwrite user work or discard uncommitted changes.
- Never execute destructive database commands without explicit authorization.
- Do not run production migrations or modify production data.
- Do not expose secrets in output, logs, test fixtures, or documentation.
- If a key behavior is ambiguous, search for evidence and then ask or record the uncertainty; do not guess.

## After editing

- Review the complete diff, not only the new code.
- Run targeted tests first, then broader checks as appropriate.
- Check whether a Flyway migration is required and whether it is safe.
- Verify error handling, authorization, transaction boundaries, concurrency, and retry safety.
- Update API contracts, blueprint, and decision log when relevant.
- Report exact commands and results. Separate executed checks from recommended but unexecuted checks. Push the feature branch and open a verified PR when repository access/authentication permits; do not claim success without verification.
- Never mention LLM, AI assistant, model, or tool names in branch names, commit messages/metadata, PR titles/descriptions, issue text, tags, or release notes. Do not falsify Git author identity.

## Agent decision policy

When two approaches are possible, prefer the one that:
1. preserves required behavior and data;
2. respects module ownership;
3. uses straightforward Spring/Kotlin conventions;
4. is testable against PostgreSQL;
5. has explicit failure and retry behavior;
6. introduces the fewest new concepts/dependencies;
7. can be explained and maintained by a typical Spring developer.

Do not optimize for fewer lines of code or superficial enterprise appearance.

## Handling conflicts

- User's explicit task-specific instruction can override a project preference, but not safety constraints such as secret protection or unauthorized destructive actions.
- If a task conflicts with a documented architectural decision, explain the conflict and ask for confirmation before making a broad change.
- A local implementation detail must not silently override a global invariant.
