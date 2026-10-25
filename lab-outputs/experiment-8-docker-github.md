# Experiment 8 — Docker, Docker Compose, and GitHub

## 1. Experiment

**Experiment number:** 8  
**Title:** Docker, Docker Compose, and GitHub

The Experiment 8 manual was not provided in the shared files. This report follows the Docker/GitHub requirements in the task and does not claim additional manual-specific requirements.

## 2. Aim

Deploy the application using Docker and GitHub; deploy a full-stack application on a cloud platform and present the capstone.

## 3. Requirements and tools

- Docker Desktop with Docker Compose v2 for local containers.
- A GitHub repository and GitHub Actions.
- Java 17/Maven and Node.js/npm for local checks.
- Environment-based MySQL credentials and a private JWT secret.
- A Linux Docker host, provider configuration, domain, HTTPS certificate, and cloud secrets for an actual public deployment.

## 4. Theory

Docker packages services and their runtime dependencies into images. Docker Compose declares how the MySQL database, Spring Boot API, and React/Nginx frontend are configured, networked, and started. A named MySQL volume preserves database files across container recreation. GitHub Actions can test and build changes on hosted runners; a protected workflow can publish successful container images to GHCR. Image publishing does not itself deploy or host a running public application. A real cloud deployment also needs a configured host, secrets, persistent storage, network access, and HTTPS.

## 5. Implementation and important files

- `backend/Dockerfile` — Maven build stage and Spring Boot Java runtime, with an API health check.
- `petivo-frontend/Dockerfile` — Vite production build and Nginx runtime.
- `petivo-frontend/nginx.conf` — serves the React app and proxies `/api/` to the backend service.
- `docker-compose.yml` — MySQL 8.4, backend, frontend, readiness checks, persistent `mysql_data`, and environment variables.
- `backend/database.sql` — schema and idempotent public service catalog initialization; it does not create demo users/passwords.
- `.env.example` — placeholders only for local credentials and GHCR image names.
- `.gitignore`, `backend/.dockerignore`, and `petivo-frontend/.dockerignore` — exclude local secrets and generated build contents from Git or build contexts.
- `.github/workflows/build-and-publish.yml` — backend tests, frontend lint/build, Compose config validation, Docker builds, and main-branch-only GHCR publishing.
- `README.md`, `RUN_ORDER.txt`, and `LIVE_DEMO_CHECKLIST.txt` — setup and demonstration steps.

## 6. Commands

First-time setup in PowerShell:

```powershell
Copy-Item .env.example .env
notepad .env
```

Replace the password/JWT placeholders with private values. Do not commit or share `.env`. Set both `PETIVO_ADMIN_EMAIL` and `PETIVO_ADMIN_PASSWORD` only if an administrator should be provisioned on first startup.

Docker commands (run from the project root):

```powershell
docker compose config
docker compose build
docker compose up -d
docker compose ps
docker compose logs --tail=100
docker compose down
```

`docker compose down` preserves the database volume. This experiment did not run `docker compose down -v`.

To test frontend and backend without Docker:

```powershell
Set-Location backend
.\mvnw.cmd -B test
Set-Location ..\petivo-frontend
npm run lint
npm run build
```

No commit, merge, or push commands were executed. See the safe commit and push plan in `OUTPUTS-CHECKLIST.md`; inspect and authorize those operations yourself.

## 7. Expected output and verified actual output

**Expected after Docker is installed and `.env` is configured:** `docker compose config` exits successfully; all three services start and become healthy; `http://localhost:8080` serves PETIVO; service/API requests work; MySQL data remains after `docker compose down` and a later `up -d`.

**Verified actual in this environment:**

- Maven: **20 tests, 0 failures, 0 errors, 0 skipped**; the backend package command completed successfully.
- Frontend: ESLint passed; Vite production build passed.
- Docker CLI/Compose: **BLOCKED** — Docker is not installed, so Compose configuration, image builds, container health, and persistent-volume behavior were not run.
- GitHub Actions/GHCR: **NOT RUN** — the local workflow is not on GitHub yet. The remote `main` branch currently has one initial README-only commit; no local commit was created and nothing was pushed.
- Cloud URL: **NOT DEPLOYED** — no cloud provider/host, domain, TLS certificate, or cloud credentials were supplied.

## 8. Test cases

| Test/check | Result |
|---|---|
| Backend Maven test suite | PASS — 20/20 |
| Frontend lint | PASS |
| Frontend production build | PASS |
| Docker Compose config validation | BLOCKED — Docker unavailable |
| Backend/frontend container image builds | BLOCKED — Docker unavailable |
| Running service status and health checks | BLOCKED — Docker unavailable |
| GitHub Actions workflow run and GHCR images | NOT RUN — workflow has not been pushed |
| Public cloud deployment | NOT RUN — cloud environment not configured |

## 9. Conclusion

The project now contains Dockerfiles, a Compose stack, environment templates, ignore rules, readiness checks, and a CI/publish workflow. Local code checks pass, but container execution, GitHub Actions/GHCR publishing, and cloud hosting remain unverified. The local project has an uncommitted staged baseline and an existing remote history; preserve and merge both before pushing.

## 10. Viva questions and answers

1. **Why use a named Docker volume for MySQL?**  
   It stores database files outside a replaceable container so normal container recreation does not erase the data.
2. **What does GitHub Actions do in this setup?**  
   It runs tests and builds on CI runners, validates Docker images, and publishes images to GHCR after a successful push to `main`.
3. **Does publishing an image to GHCR mean the app is deployed?**  
   No. A cloud host must pull and run the images and be configured with secrets, networking, persistent storage, and HTTPS.
