# Git, Branch, Commit, Push, and Pull Request Workflow

These rules are mandatory for code, tests, configuration, and documentation changes that are part of a feature or fix. A local implementation is not considered delivered until its branch is pushed and a suitable pull request is opened, unless repository access/authentication or an explicit user instruction prevents it.

## 1. Protect existing work first

Before any Git operation that changes the repository:
1. Run `git status --short --branch` and inspect the full status.
2. Inspect the current branch, remotes, default branch, and relevant recent history.
3. Preserve all pre-existing user changes. Never discard, reset, stash, stage, or commit user changes without understanding them and obtaining approval where needed.
4. Do not work directly on `main`, `master`, or the repository's default/protected branch.
5. Do not use destructive commands such as `git reset --hard`, `git clean -fd`, or force-push to get past a conflict.

If the working tree contains unrelated or uncommitted changes, avoid including them in the feature commit. If a clean branch cannot be made safely, stop and report the blocker instead of destroying or absorbing unrelated work.

## 2. One coherent feature/fix per branch and PR

- Create a dedicated branch for each independently reviewable feature, bug fix, refactor, test-only change, or docs change. Do not combine unrelated features in one branch/PR.
- Start the branch from the current default/integration branch after fetching and verifying the correct base, when network access and repository state allow it.
- Use short, descriptive, lowercase kebab-case branch names with a type prefix:
  - `feat/refresh-token-rotation`
  - `fix/catalog-search-pagination`
  - `refactor/cart-domain-boundary`
  - `test/checkout-idempotency`
  - `docs/kotlin-spring-guidelines`
  - `build/align-kotlin-plugins`
  - `ci/gradle-quality-checks`
- Include the relevant module in the slug when helpful, e.g. `feat/identity-refresh-token-rotation`.
- Keep the branch scoped to one feature and its necessary tests/docs/migration. If the work discovers an unrelated issue, record it separately and create another branch/PR rather than expanding the current branch without a reason.
- Never push feature work directly to the default/protected branch.

## 3. Push and open a pull request

For each finished feature branch:
1. Review `git diff` and `git diff --cached`; check for secrets, unrelated files, generated artifacts, local configuration, and accidental data.
2. Run applicable formatter, static checks, unit tests, integration tests, and build tasks; record exact results.
3. Commit only the intended feature files using Conventional Commits.
4. Fetch/rebase or merge the latest target branch only when safe and appropriate; resolve conflicts deliberately. Never force-push as a routine shortcut.
5. Push the feature branch to the repository's configured GitHub remote.
6. Open a pull request to the correct default/integration branch, using `gh pr create` or an available approved GitHub integration when available and authenticated.
7. Verify the remote branch and PR URL/status after the command/tool reports success. Do not state a PR exists unless it was confirmed.
8. Leave the PR open for review. Do not merge it, enable auto-merge, approve it on behalf of another person, or delete the branch unless explicitly authorized by the user or repository automation.

If `gh` is missing, the remote is unavailable, permissions are insufficient, or authentication fails, do not invent success. Finish and validate the local branch if safe, then report the exact blocker and the commands/actions still needed. Do not print tokens, credentials, or environment secrets while diagnosing authentication.

## 4. Conventional Commits

Use the Conventional Commits format:

```text
<type>(<scope>): <imperative description>

[optional body]

[optional footer(s)]
```

Use the following types where applicable:
- `feat`: add user-visible or business functionality;
- `fix`: correct a defect;
- `refactor`: restructure without intentional behavior change;
- `test`: add or improve tests;
- `docs`: documentation only;
- `build`: build/dependency configuration;
- `ci`: CI workflow changes;
- `perf`: performance improvement;
- `chore`: maintenance that does not fit a more specific type.

Examples:
- `feat(identity): rotate refresh tokens safely`
- `fix(catalog): preserve search pagination semantics`
- `test(checkout): cover duplicate idempotency keys`
- `refactor(cart): isolate domain rules from persistence`
- `docs(migration): document API compatibility checks`
- `build(kotlin): align Spring compiler plugins`

Commit message rules:
- Describe the actual change clearly and concisely; use imperative wording where natural.
- Keep each commit logically coherent. Do not create meaningless commits like `changes`, `update`, `final`, or `fix stuff`.
- Include `BREAKING CHANGE: ...` in the footer or `!` after type/scope when a real breaking change is intentional; document the impact.
- Do not add fake issue references, test claims, trailers, signatures, or co-author attributions.
- Never alter the configured Git author identity to impersonate a person. Preserve the repository/user's configured author identity.

## 5. No LLM/tool attribution in repository metadata

Do **not** mention the name of any LLM, AI assistant, coding agent, or generation tool in:
- branch names or tags;
- commit subjects or bodies;
- PR titles or descriptions;
- issue titles/bodies created for this work;
- changelog/release-note entries for the work;
- comments added solely to attribute generated code;
- commit trailers or co-author lines.

Do not use boilerplate such as “Generated by …”, “AI-assisted by …”, model names, or tool names in those fields. Use professional descriptions of the code change, behavior, test results, and operational impact instead. This rule does **not** authorize falsifying the configured Git identity, forging signatures, or claiming human review/approval that did not happen. Do not modify author/committer metadata to conceal or impersonate anyone; simply omit LLM/tool attribution from the specified metadata.

## 6. Pull request title and description

Use a clear title that describes the behavior, for example:
- `feat(identity): implement refresh-token rotation`
- `fix(catalog): preserve PostgreSQL search behavior`
- `docs: add Kotlin and Spring Boot agent guidelines`

Use a PR description with these sections as applicable:

```markdown
## Summary
- What changed and why.

## Implementation
- Main implementation decisions and module boundaries.

## API and data compatibility
- Contract behavior preserved or intentional changes.
- Database migration impact, or "None".

## Validation
- Exact commands run and their results.
- Clearly list checks that were not run and why.

## Security and concurrency
- Authorization, idempotency, transaction, and retry considerations.

## Risks / follow-up
- Known limitations, rollout notes, and follow-up work.
```

- Link an issue only if the issue exists and is relevant.
- Do not claim all checks passed when some were not run.
- Do not include LLM/tool names or claims of generated authorship in the PR title/body.
- Avoid an empty or generic PR description; provide a reviewable summary tied to actual changes.

## 7. Merge and completion policy

- Creating a PR is required; merging is not automatically authorized.
- Treat passing CI, required reviews, branch protection, and the repository's merge policy as authoritative. Do not bypass them.
- Do not suppress or weaken failing checks merely to merge.
- After the PR is opened, return its verified URL, source/target branches, commits included, checks run, and any blockers.
- If pushing or PR creation is impossible, state that explicitly and provide the local branch name/status and exact remaining step. Never describe unpushed changes as uploaded to GitHub.

## Official references

- Conventional Commits 1.0.0: https://www.conventionalcommits.org/en/v1.0.0/
- GitHub protected branches: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches
- GitHub pull requests and repository rulesets: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository
