# PETIVO Pet Care Management System

PETIVO is a React + Vite frontend connected to a Spring Boot backend using
Spring Data JPA and MySQL.

## What this version demonstrates

- React + Vite frontend
- Spring Boot REST API
- Spring Data JPA repository
- MySQL database persistence
- JWT-based User/Admin authentication
- Role-protected API endpoints
- Frontend bearer-token integration
- Environment-based production credentials and JWT signing key
- Appointment CREATE
- Appointment READ
- Appointment UPDATE
- Appointment DELETE

## Project structure

backend/
- Spring Boot API
- JPA entity and repository
- MySQL configuration

petivo-frontend/
- React/Vite interface
- Axios API calls
- Appointment booking/edit/delete UI

## Run with Docker Compose

Install and start Docker Desktop, then make a local environment file:

    Copy-Item .env.example .env

Replace all password and JWT placeholders in `.env` with private values. For
an administrator account, set both `PETIVO_ADMIN_EMAIL` and
`PETIVO_ADMIN_PASSWORD`; leave both empty to disable administrator bootstrap.
The bootstrap only creates a new administrator and does not reset existing
accounts.

Run the stack from the project root:

    docker compose config
    docker compose build
    docker compose up -d
    docker compose ps

Open `http://localhost:8080`. The frontend uses a same-origin `/api` proxy to
reach Spring Boot. MySQL data persists in the `mysql_data` volume.

Useful operations:

    docker compose logs --tail=100
    docker compose down

If a service is not healthy, inspect its logs with
`docker compose logs --tail=100 mysql backend frontend` and check
`docker compose ps`. If port 8080 is already in use, change `APP_PORT` in
`.env` and open the corresponding host port. The frontend and API are served
from the same origin in Compose; if hosting them on separate origins, set
`CORS_ALLOWED_ORIGINS` to the exact trusted frontend origin(s).

Do not use `docker compose down -v` unless you intentionally want to delete
the persisted database volume. MySQL initialization uses
`backend/database.sql` when the database volume is first created; Hibernate
then updates the schema without dropping existing tables.

## Accounts and JWT security

The backend verifies credentials with BCrypt and returns a signed JWT from
`POST /api/auth/login`. The frontend stores the token and sends it as a
`Bearer` token in the `Authorization` header for API requests. On startup, the
frontend calls `GET /api/auth/me` to validate a saved token.

`POST /api/auth/register` and `POST /api/auth/login` are public. Reading the
service catalog with `GET /api/services` is public. Profile, pet, appointment,
and other API routes require a valid JWT; `/api/admin/**` and service write
operations require the `ADMIN` role. Unauthenticated requests receive HTTP 401,
and authenticated users without the required role receive HTTP 403.
Pet list and detail reads map the lazy owner relationship inside a read-only
service transaction because Open Session in View is disabled.

The backend integration tests exercise token issuance, protected routes,
invalid/missing tokens, role authorization, and the public service catalog.
Run them from `backend` with:

    .\mvnw.cmd -B test

Production startup requires `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`
environment variables. The JWT key must be at least 32 bytes. Never commit
`.env` or use a public/default administrator password. Ordinary users can
register through the interface; administrator bootstrap is opt-in using the
two `PETIVO_ADMIN_*` environment variables.
Previously created demo accounts in an existing database are not deleted or
reset automatically to preserve stored data. Rotate or disable any such
accounts manually before exposing an old database to the internet.

## Run checks

From `backend` on Windows:

    .\mvnw.cmd -B test

From `petivo-frontend`:

    npm ci
    npm run lint
    npm run build

The same tests and frontend checks run in GitHub Actions. Docker image builds
are also validated in CI. Publishing to GHCR is configured for pushes to the
`main` branch; it does not deploy the app to a cloud host by itself.

## Experiment: React and Spring Boot API integration

**AIM:** Connect the React front end with Spring Boot backend APIs.

**LEARNING OUTCOME:** Integrate the front end and back end through secure API
endpoints.

**THEORY:** A full-stack web application separates the presentation layer from
the application and data layers. React renders the user interface and sends
HTTP requests to REST endpoints; Spring Boot receives those requests, applies
business rules, and uses Spring Data JPA to persist data in MySQL. JSON is used
as the request and response format. The Axios service in this project centralizes
the API base URL and endpoint calls. Authentication endpoints issue a signed
JWT after validating credentials, and the client sends that token in the
`Authorization: Bearer` header for protected requests. Spring Security checks
authentication and role permissions before allowing access. Passwords are
stored with BCrypt rather than as plain text. This separation supports
maintainable interfaces, reusable APIs, and server-side access control.

**Implementation in PETIVO:** The React application calls the Spring Boot API
through `petivo-frontend/src/services/apiService.js`. The appointment screen
demonstrates create, read, update, and delete operations. Login and registration
are public; protected profile, pet, appointment, and administrative routes
require valid authorization according to their configured roles.

## Experiment: Docker and GitHub deployment

**AIM:** Deploy the application using Docker and GitHub.

**LEARNING OUTCOME:** Deploy a full-stack application to a cloud platform and
present the capstone.

**THEORY:** Docker packages an application and its runtime dependencies into
portable images. Containers run consistently across development and deployment
environments, while Docker Compose describes how related services are built,
networked, configured, and started together. In this project, separate images
run the Spring Boot API and the built React site; MySQL runs as a separate
service with a persistent volume. The frontend web server forwards `/api`
requests to the backend, so browser API calls remain same-origin. GitHub stores
the source code and runs GitHub Actions when changes are pushed or a pull
request is opened. The workflow tests the backend, builds the frontend, and
publishes versioned container images to GitHub Container
Registry (GHCR). A cloud Docker host can then pull these images and run the
Compose stack. Production deployments must keep database credentials and the
JWT signing key in environment variables or the cloud platform's secret store,
use HTTPS, and retain persistent database storage.

**Run locally with Docker:** Copy `.env.example` to `.env`, replace every
placeholder with strong values, and start Docker Desktop. Then run:

    docker compose up --build

Open `http://localhost:8080`. The MySQL data is stored in the `mysql_data`
Docker volume and remains available when containers are recreated. Stop the
stack with `docker compose down`. This leaves the database volume intact.

**Publish through GitHub:** Push the project to a GitHub repository using the
`main` branch. GitHub Actions runs backend tests and frontend checks on pushes
and pull requests. On a successful push to `main`, it also publishes the
backend and frontend images to GHCR. The workflow needs the repository's
standard `GITHUB_TOKEN` permissions (`contents: read`, `packages: write`).

**Deploy to a cloud Docker host:** Create a Linux virtual machine or managed
container host with Docker Engine and the Docker Compose plugin. Clone the
GitHub repository there, configure `.env` with production-only database
credentials and a unique JWT secret, set `GHCR_OWNER` and `GHCR_REPOSITORY` to
the lowercase GitHub owner and repository name, and allow inbound HTTPS traffic
(and HTTP only if it is redirected to HTTPS). If the GHCR packages are private,
authenticate the host to GHCR with a GitHub token that has `read:packages`.
Then pull and start the published images:

    docker compose pull
    docker compose up -d

Configure the cloud host's HTTPS reverse proxy or load balancer to forward
traffic to the frontend's configured `APP_PORT`. Keep the MySQL volume or use a
managed MySQL service with backups. A real public deployment also requires a
domain name, TLS certificate, cloud credentials, and provider-specific network
and secret configuration; these must be supplied by the project owner.

## MySQL

Database:
    petivo_db

The database username and password are supplied through `DB_USERNAME` and
`DB_PASSWORD`; no database password is stored in source configuration. When
running the backend outside Compose, export those variables and `JWT_SECRET`
in the shell before starting Spring Boot.

The JPA setting `spring.jpa.hibernate.ddl-auto=update` allows Hibernate to create/update
the `appointments` table from the Appointment entity.

## Assignment 4 relevance

The backend uses:
`AppointmentRepository extends JpaRepository<Appointment, Long>`

The appointment API demonstrates the four CRUD operations required for database
operations using a JPA Repository.

## Important

The sidebar/shop/order screens contain UI placeholders from the earlier frontend.
The database-backed feature implemented for this package is the appointment module.

## Experiment reports

Implementation notes, commands, verified checks, expected outputs, and screenshot
guidance are in [`lab-outputs/`](./lab-outputs/).
