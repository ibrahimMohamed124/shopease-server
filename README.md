# ShopEase AI Agent Rules Pack

This folder contains project instructions for AI coding agents migrating ShopEase from NestJS + Prisma to Kotlin + Spring Boot.

## Install

Copy these files into the root of the **new `shopease-server` repository**, preserving the `docs/` paths:

```text
shopease-server/
├── AGENTS.md
├── README.md
└── docs/
    ├── architecture/
    │   ├── ARCHITECTURE.md
    │   ├── DOMAIN_BOUNDARIES.md
    │   ├── DATABASE_AND_MIGRATIONS.md
    │   └── SECURITY.md
    ├── migration/
    │   ├── MIGRATION_PLAYBOOK.md
    │   └── MIGRATION_BLUEPRINT_TEMPLATE.md
    └── quality/
        ├── TESTING.md
        ├── API_COMPATIBILITY.md
        ├── KOTLIN_SPRING_GUIDELINES.md
        ├── GIT_WORKFLOW.md
        └── AI_AGENT_WORKFLOW.md
```

If these files are delivered as a ZIP, extract its contents into the repository root. Do not replace an existing `AGENTS.md` without reviewing and merging its instructions.

## First task for an AI agent

Ask the agent to **analyze only**, without modifying code, then produce `docs/migration/MIGRATION_BLUEPRINT.md` based on the template and evidence from both repositories. It must inventory actual endpoints, Prisma/database details, auth rules, feature status, tests, and migration risks.

After reviewing the blueprint, migrate one vertical slice at a time. Update the blueprint as implementation and verification progress. For each feature/fix/docs change, follow `docs/quality/GIT_WORKFLOW.md`: use a dedicated branch, Conventional Commits, push to GitHub, and open a reviewable PR when credentials and repository permissions allow. Do not merge without authorization and do not put LLM/tool names in branch, commit, issue, or PR metadata.

For Kotlin/Spring-specific implementation decisions, follow `docs/quality/KOTLIN_SPRING_GUIDELINES.md` and the official documentation for the exact dependency versions pinned by the project.

## Important

These documents are guardrails, not proof that the current code follows them. The agent must inspect the actual repositories, verify dependency versions, and report uncertainty. No instruction file can guarantee that every agent will obey every rule; use code review, automated tests, CI, and repository protections to enforce critical requirements.
