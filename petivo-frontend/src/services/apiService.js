import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const API = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Attach JWT token to requests if available
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("petivo_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// Response interceptor for session expiration
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Don't auto-redirect if on the login request itself
      const url = error.config?.url || "";
      if (!url.includes("/auth/login") && !url.includes("/auth/register")) {
        localStorage.removeItem("petivo_token");
        localStorage.removeItem("petivo_user");
        window.dispatchEvent(new Event("petivo:unauthorized"));
      }
    }
    return Promise.reject(error);
  },
);

// --- Auth APIs ---
export const login = (role, username, password) =>
  API.post("/auth/login", { role, username, password });

export const register = (name, email, password) =>
  API.post("/auth/register", { name, email, password });

export const getMe = () => API.get("/auth/me");

// --- Profile APIs ---
export const getMyProfile = () => API.get("/users/me");
export const updateMyProfile = (profile) => API.put("/users/me", profile);

// --- Pet Management APIs ---
export const getPets = () => API.get("/pets");
export const getPet = (id) => API.get(`/pets/${id}`);
export const createPet = (pet) => API.post("/pets", pet);
export const updatePet = (id, pet) => API.put(`/pets/${id}`, pet);
export const deletePet = (id) => API.delete(`/pets/${id}`);

// --- Services APIs ---
export const getServices = (activeOnly = true) =>
  API.get(`/services?activeOnly=${activeOnly}`);
export const createService = (service) => API.post("/services", service);
export const updateService = (id, service) =>
  API.put(`/services/${id}`, service);
export const deleteService = (id) => API.delete(`/services/${id}`);

// --- Appointment APIs ---
export const getAppointments = () => API.get("/db/appointments");
export const createAppointment = (appointment) =>
  API.post("/db/appointments", appointment);
export const updateAppointment = (id, appointment) =>
  API.put(`/db/appointments/${id}`, appointment);
export const deleteAppointment = (id) => API.delete(`/db/appointments/${id}`);

// --- Admin APIs ---
export const getAdminStats = () => API.get("/admin/stats");
export const getAllUsers = () => API.get("/users");

export default API;
