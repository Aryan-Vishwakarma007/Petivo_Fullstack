# Experiment 7 — React Frontend Connected to Spring Boot APIs

## 1. Experiment

**Experiment number:** 7  
**Title:** React Frontend Connected to Spring Boot APIs

## 2. Aim

Connect the React front end with Spring Boot backend APIs and integrate the front end and back end through secure API endpoints.

## 3. Requirements and tools

- Node.js 22 and npm.
- Java 17, the Maven wrapper, and a configured MySQL database for a live local run.
- React, Vite, Axios, Spring Boot REST, Spring Security, Spring Data JPA, and MySQL.
- Docker Compose may be used to start the frontend, API, and database together.

## 4. Theory

React implements the presentation layer, while Spring Boot exposes REST endpoints for authentication and application data. Axios sends and receives JSON over HTTP. The frontend uses `VITE_API_URL` to select the API base URL and attaches the JWT bearer token to authenticated requests. Spring Security validates the token and role before the controller performs application logic and stores data through Spring Data JPA. CORS allows only the configured development origins; in the Compose setup the browser uses a same-origin `/api` path and Nginx proxies requests internally to Spring Boot.

## 5. Implementation and important files

- `petivo-frontend/src/services/apiService.js` — Axios instance, environment-based API URL, bearer-token interceptor, session-expiry handling, and endpoint methods.
- `petivo-frontend/src/components/Login.jsx` — login and registration against the real API; no demo password display or credential logging.
- `petivo-frontend/src/App.jsx` — validates a saved token, loads appointments and pets from the API, and reports request failures.
- `petivo-frontend/src/components/Grooming.jsx` — loads the service catalog and pet list from the backend; it no longer silently substitutes a mock service catalog.
- `petivo-frontend/src/components/PetList.jsx` and `BookingModal.jsx` — pet and appointment form interactions.
- `backend/src/main/java/com/petivo/api/controller/ServiceController.java` — public `GET /api/services`.
- `backend/src/main/java/com/petivo/api/controller/AppointmentDbController.java` — appointment CRUD endpoints.
- `backend/src/main/java/com/petivo/api/config/SecurityConfig.java` and `CorsConfig.java` — API access rules and development CORS origins.
- `petivo-frontend/Dockerfile` and `petivo-frontend/nginx.conf` — production build and same-origin `/api` proxy.

## 6. Commands

Frontend checks:

```powershell
Set-Location petivo-frontend
npm run lint
npm run build
```

Backend endpoint and integration checks:

```powershell
Set-Location backend
.\mvnw.cmd -B test
```

Live Docker integration, when Docker Desktop and `.env` are configured:

```powershell
Set-Location ..
docker compose config
docker compose build
docker compose up -d
docker compose ps
```

Open `http://localhost:8080`, register/sign in, then use Grooming, Pet List, and Appointments.

## 7. Example endpoint response and verified actual output

**Example response shape for `GET /api/services`** (the database-generated `id` can differ):

```json
[
  {
    "id": 1,
    "name": "Basic Bath",
    "description": "A refreshing bath, gentle shampoo, and towel drying.",
    "price": 399.00,
    "active": true
  }
]
```

This shows the endpoint's DTO shape and a service inserted by the project's service-catalog initializer; it is an illustrative response, not a captured live MySQL response.

**Verified actual:** Frontend ESLint completed with no reported issues, and `npm run build` produced the Vite production bundle. Backend MockMvc tests returned HTTP 200 from the actual Spring `GET /api/services` controller and asserted that the JSON contained `Basic Bath`; appointment CRUD and JWT-protected routes are covered by backend integration tests. The exact endpoint response was not captured from a live Docker/MySQL server because Docker is not installed in this environment. A browser-to-running-API demonstration remains to be performed locally.

## 8. Test cases

| Test/check | Result |
|---|---|
| Frontend lint (`npm run lint`) | PASS |
| Frontend production build (`npm run build`) | PASS |
| Public service catalog endpoint returns JSON containing seeded service | PASS (MockMvc/H2 integration test) |
| Authenticated appointment read/create endpoints | PASS (backend integration tests) |
| Browser UI communicating with running Compose stack | NOT RUN — Docker unavailable |
| Real MySQL persistence from the browser | NOT RUN — Docker/live DB credentials not configured for this validation |

## 9. Conclusion

The React application calls real Spring Boot endpoints and includes authenticated requests and error handling. Lint, production build, and backend API tests pass. The complete live browser/database flow still needs to be demonstrated after starting the stack with Docker or a correctly configured local MySQL server.

## 10. Viva questions and answers

1. **What is a REST API?**  
   It is an HTTP interface in which resources are accessed using methods such as GET, POST, PUT, and DELETE.
2. **Why configure an API base URL with `VITE_API_URL`?**  
   It separates the frontend build from a fixed API host; Docker uses `/api`, while Vite development can use the configured local API URL.
3. **What does CORS control?**  
   It controls which browser origins may make cross-origin requests; it is not a substitute for server-side authentication or authorization.
