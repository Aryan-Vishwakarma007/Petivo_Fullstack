# Experiment 6 — JWT Authentication in Spring Boot

## 1. Experiment

**Experiment number:** 6  
**Title:** JWT Authentication in Spring Boot

## 2. Aim

Implement and demonstrate JWT authentication in the existing Spring Boot application.

## 3. Requirements and tools

- Java 17 and the existing Maven wrapper.
- Spring Boot, Spring Security, JJWT, BCrypt, and the existing H2 test database.
- A MySQL database for a local non-test run.
- For production, `DB_USERNAME`, `DB_PASSWORD`, and a private `JWT_SECRET` of at least 32 bytes.
- A standard account registered through PETIVO, or an administrator explicitly provisioned using both `PETIVO_ADMIN_EMAIL` and `PETIVO_ADMIN_PASSWORD`.

There are no default demo login credentials. Test-only fixture credentials are used only in automated tests against the H2 test database.

## 4. Theory

JWT authentication is a stateless way to authenticate API requests. After Spring Security verifies the submitted username and BCrypt password hash, the API signs a token containing the user's identity, role, issue time, and expiration. The React client sends the token in the `Authorization: Bearer` header. The Spring Security filter verifies the signature and expiry before allowing access to protected routes. Stateless session management means the server does not use an HTTP session to remember authentication. Invalid, expired, and missing tokens are rejected with HTTP 401; authenticated users without the required role receive HTTP 403.

## 5. Implementation and important files

- `backend/src/main/java/com/petivo/api/service/AuthService.java` — BCrypt password checking and login/registration behavior.
- `backend/src/main/java/com/petivo/api/security/JwtTokenProvider.java` — HS256 token generation, validation, expiry checks, and minimum secret length.
- `backend/src/main/java/com/petivo/api/security/JwtAuthenticationFilter.java` — extracts and verifies bearer tokens for protected requests.
- `backend/src/main/java/com/petivo/api/config/SecurityConfig.java` — public routes, protected routes, roles, CORS, and stateless sessions.
- `backend/src/main/java/com/petivo/api/config/DataInitializer.java` — opt-in administrator bootstrap; no seeded default account.
- `backend/src/test/java/com/petivo/api/JwtAuthenticationIntegrationTest.java` — HTTP-level JWT, CORS, role, and service-catalog tests.
- `backend/src/test/resources/application.properties` — test-only H2 configuration and test signing key; not for production.

## 6. Commands and request examples

Run the full backend test suite on Windows:

```powershell
Set-Location backend
.\mvnw.cmd -B test
```

To run Spring Boot directly against an already configured local MySQL server,
enter database credentials securely in PowerShell and generate a fresh JWT key:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/petivo_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
$env:DB_USERNAME = Read-Host 'MySQL username'
$secureDbPassword = Read-Host 'MySQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secureDbPassword).Password
$jwtBytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
Set-Location backend
.\mvnw.cmd spring-boot:run
```

To create an administrator on a fresh database, securely provide both
`PETIVO_ADMIN_EMAIL` and `PETIVO_ADMIN_PASSWORD` before starting Spring Boot.
The password can be collected using `Read-Host -AsSecureString` as above.

For the Compose demo, configure the local `.env` first and start the services from the project root:

```powershell
docker compose up -d
```

Example requests use placeholders only. Replace `<REGISTERED_EMAIL>` and `<YOUR_PASSWORD>` locally; do not paste credentials or token values into reports or screenshots.

```powershell
$base = 'http://localhost:8080'
$loginBody = @{
  role = 'user'
  username = '<REGISTERED_EMAIL>'
  password = '<YOUR_PASSWORD>'
} | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" `
  -ContentType 'application/json' -Body $loginBody
$token = $login.token
Invoke-RestMethod -Uri "$base/api/auth/me" `
  -Headers @{ Authorization = "Bearer $token" }
```

Expected negative checks:

```powershell
# Missing bearer token: expect HTTP 401.
Invoke-WebRequest -Uri "$base/api/users/me"

# Invalid placeholder token: expect HTTP 401.
Invoke-WebRequest -Uri "$base/api/users/me" `
  -Headers @{ Authorization = 'Bearer invalid-demo-placeholder' }
```

## 7. Expected output and verified actual output

**Expected:** Valid credentials return HTTP 200 and a JWT; a valid token can read the current profile; wrong credentials and missing, malformed, or expired tokens return HTTP 401; a regular user receives HTTP 403 from an administrator-only endpoint.

**Verified actual:** `.\mvnw.cmd -B package` completed successfully. The JWT integration test suite reports **10 tests, 0 failures, 0 errors, 0 skipped**, covering valid login, wrong password, protected access, absent/invalid/expired tokens, role authorization, permitted and rejected CORS origins, public service catalog data, and absence of default demo accounts. These ran through Spring's MockMvc integration tests with H2; they were not a live Docker/MySQL run.

## 8. Test cases

| Test | Result |
|---|---|
| Valid login issues a token and allows protected profile access | PASS |
| Invalid login password is rejected with HTTP 401 | PASS |
| Protected profile and appointment routes reject a missing token | PASS |
| Protected profile rejects a malformed token | PASS |
| Protected profile rejects a correctly signed but expired token | PASS |
| USER is denied admin endpoint; ADMIN is allowed | PASS |
| Vite development origin passes the configured CORS preflight | PASS |
| An unconfigured cross-origin request is rejected | PASS |
| Public services endpoint returns catalog JSON | PASS |
| Fresh test database receives no default demo accounts | PASS |

## 9. Conclusion

PETIVO uses BCrypt for stored passwords and stateless JWT authentication for protected API endpoints. The configured automated tests verify successful authentication and the required rejection cases. Production still requires private environment variables and a database; no production credentials are included in source.

## 10. Viva questions and answers

1. **Why use JWT with stateless sessions?**  
   The API can verify each request from the signed token without storing a server-side login session.
2. **Why hash passwords with BCrypt?**  
   BCrypt is a salted, adaptive password hash, making stored password values unsuitable for direct password recovery and more expensive to brute-force.
3. **What is the difference between HTTP 401 and HTTP 403?**  
   HTTP 401 means the request is unauthenticated or has an invalid credential; HTTP 403 means the user is authenticated but lacks permission.
