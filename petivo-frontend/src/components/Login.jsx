import { useState } from "react";
import { login, register } from "../services/apiService";

function Login({ onLogin, initialError = "" }) {
  const [isRegistering, setIsRegistering] = useState(false);
  const [role, setRole] = useState("user");

  // Login inputs
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // Register inputs
  const [regName, setRegName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPassword, setRegPassword] = useState("");

  const [error, setError] = useState(initialError);
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccessMsg("");

    try {
      const response = await login(role, username, password);
      if (response.data?.success && response.data.token && response.data.user) {
        localStorage.setItem("petivo_token", response.data.token);
        localStorage.setItem("petivo_user", JSON.stringify(response.data.user));
        onLogin(response.data.role, response.data.user);
      } else {
        setError(response.data?.message || "Invalid username or password.");
      }
    } catch (err) {
      const serverMsg = err.response?.data?.message || err.response?.data?.error;
      setError(serverMsg || "Backend is not running. Please start Spring Boot on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccessMsg("");

    if (regPassword.length < 6) {
      setError("Password must be at least 6 characters long.");
      setLoading(false);
      return;
    }

    try {
      const response = await register(regName, regEmail, regPassword);
      if (response.data && response.data.success) {
        setSuccessMsg("Account created successfully! You can now log in.");
        setIsRegistering(false);
        setUsername(regEmail);
        setPassword(regPassword);
      } else {
        setError(response.data?.message || "Registration failed. Please try again.");
      }
    } catch (err) {
      const serverMsg = err.response?.data?.message || err.response?.data?.error;
      setError(serverMsg || "Could not register. Ensure email is unique and backend is running.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="card shadow-sm border-0 mx-auto" style={{ maxWidth: 440, borderRadius: 16 }}>
        <div className="card-body p-4">
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary mb-1">🐾 PETIVO</h2>
            <p className="text-muted small">Pet Care Management System</p>
          </div>

          {/* Toggle between Login and Register */}
          <div className="nav nav-pills nav-fill mb-3 bg-light p-1 rounded-pill">
            <button
              type="button"
              className={`nav-link rounded-pill ${!isRegistering ? "active" : ""}`}
              onClick={() => {
                setIsRegistering(false);
                setError("");
              }}
            >
              Sign In
            </button>
            <button
              type="button"
              className={`nav-link rounded-pill ${isRegistering ? "active" : ""}`}
              onClick={() => {
                setIsRegistering(true);
                setError("");
                setSuccessMsg("");
              }}
            >
              Register
            </button>
          </div>

          {error && <div className="alert alert-danger py-2 small">{error}</div>}
          {successMsg && <div className="alert alert-success py-2 small">{successMsg}</div>}

          {!isRegistering ? (
            /* Login Form */
            <form onSubmit={handleLoginSubmit}>
              <div className="btn-group w-100 mb-3" role="group">
                <button
                  type="button"
                  className={`btn ${role === "user" ? "btn-primary" : "btn-outline-primary"}`}
                  onClick={() => setRole("user")}
                >
                  User
                </button>
                <button
                  type="button"
                  className={`btn ${role === "admin" ? "btn-primary" : "btn-outline-primary"}`}
                  onClick={() => setRole("admin")}
                >
                  Admin
                </button>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Username or Email</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Enter your username or email"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Password</label>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Enter password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <button
                type="submit"
                className="btn btn-primary w-100 py-2 fw-semibold"
                disabled={loading}
              >
                {loading ? "Signing in..." : "Sign In"}
              </button>

            </form>
          ) : (
            /* Register Form */
            <form onSubmit={handleRegisterSubmit}>
              <div className="mb-3">
                <label className="form-label small fw-semibold">Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. John Doe"
                  value={regName}
                  onChange={(e) => setRegName(e.target.value)}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Email Address</label>
                <input
                  type="email"
                  className="form-control"
                  placeholder="name@example.com"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Password</label>
                <input
                  type="password"
                  className="form-control"
                  placeholder="At least 6 characters"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
                  required
                  minLength={6}
                />
              </div>

              <button
                type="submit"
                className="btn btn-success w-100 py-2 fw-semibold"
                disabled={loading}
              >
                {loading ? "Creating Account..." : "Create Account"}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

export default Login;
