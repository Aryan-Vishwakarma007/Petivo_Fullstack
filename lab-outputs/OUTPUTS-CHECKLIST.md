# PETIVO Experiments 6–8 — Outputs Checklist

## Reports

- [x] `experiment-6-jwt.md` — JWT implementation, test outcomes, API examples, and viva questions.
- [x] `experiment-7-react-springboot.md` — React/API integration, example service response shape, checks, and viva questions.
- [x] `experiment-8-docker-github.md` — Docker/GitHub setup and explicitly separated blocked/unverified outputs.
- [x] Expected results and verified actual results are labelled separately.
- [x] The Experiment 8 manual was not attached; its report is based on the task requirements provided.

## Verified locally

- [x] Backend: Maven test suite passed, 20 tests, no failures/errors/skips.
- [x] Frontend: `npm run lint` passed.
- [x] Frontend: `npm run build` passed.
- [x] Fresh integration-test database does not get default demo users.
- [x] Local `.env` is ignored by Git; `.env.example` remains eligible for version control.
- [x] Nested `PETIVO FINAL/` repository is kept locally and ignored by the root Git repository.

## Not verified here

- [ ] Docker Compose config/build/start/health checks — Docker is not installed.
- [ ] Live browser-to-MySQL CRUD demo — requires Docker or a configured local backend/database.
- [ ] GitHub Actions/GHCR — local changes have not been pushed, by design.
- [ ] Cloud deployment — requires a cloud account/host, secrets, networking, domain, and TLS.

## Screenshot capture order

Create screenshots manually under `lab-outputs/screenshots/` after running the appropriate service:

1. **Experiment 6 — backend test summary:** Run `.\mvnw.cmd -B test` in `backend`; capture the final Maven summary and the passing JWT tests.
2. **Experiment 6 — authenticated UI:** Open PETIVO, register/sign in, and show the protected dashboard. Do not expose password fields, JWTs, or browser storage.
3. **Experiment 6 — protected request:** In browser DevTools Network, show a successful protected request and that it uses a bearer authorization header. Hide the token value and redact personal data.
4. **Experiment 7 — live service response:** Open DevTools Network, navigate to the service catalogue, select `GET /api/services`, and capture the successful status and a non-sensitive response body.
5. **Experiment 7 — CRUD persistence:** Show a created appointment, an edited appointment after refresh, and the deleted record removed. Use test data only.
6. **Experiment 8 — container status:** Run `docker compose ps` and capture the MySQL, backend, and frontend services as healthy.
7. **Experiment 8 — GitHub CI and packages:** Capture the successful Actions workflow and the two GHCR package listings after an authorized push.
8. **Experiment 8 — cloud capstone:** Capture the public HTTPS URL only after the application is actually deployed and verified.

No screenshots are included: Docker is unavailable here and GitHub/cloud deployments have not been run. Never use expected/sample output as a screenshot of actual execution.

## Safe Git handoff

Current repository facts: local branch `master` has no commit yet; the staged baseline contains the project files; `origin/main` exists and currently contains one README-only initial commit. There is a separate nested repository in `PETIVO FINAL/`; the root `.gitignore` now excludes it. No commit, merge, or push was made.

Review the full local and remote differences before pushing. A safe sequence from the root folder is:

```powershell
git status --short --branch
git diff --check
git diff --cached --check
git fetch origin
git switch -c main
git add -A
git status --short
git diff --cached --check
git diff --cached --stat
git diff --cached --name-only
```

Confirm that `.env`, tokens, generated outputs, and `PETIVO FINAL/` are absent from the staged list. Then create the local commit only when you approve:

```powershell
git commit -m "Complete PETIVO experiments 6-8"
git pull --no-rebase origin main --allow-unrelated-histories
```

The local branch and remote `main` have unrelated initial histories. If Git reports a `README.md` conflict, open the file, keep the useful content from both sides, then finish the merge:

```powershell
git status
# Resolve README.md in the editor.
git add README.md
git commit
```

Before any push, review `git log --oneline --graph --decorate -10` and `git status`. Push only after explicit approval:

```powershell
git push -u origin main
```

Do not force-push. The existing remote commit must remain in history.

## Remaining manual steps

- Install and start Docker Desktop; create `.env` from `.env.example` and set private values.
- Run the Docker commands in Experiment 8 and capture genuine outputs.
- Sign in to GitHub, review the changes, and authorize the safe merge/push before expecting Actions or GHCR images.
- Configure the cloud host, GHCR read access if images are private, production secrets, persistent database storage, inbound networking, domain, and HTTPS.
