import { useEffect, useState } from "react";

import Navbar from "./components/Navbar.jsx";
import Sidebar from "./components/Sidebar.jsx";
import UserDashboard from "./components/UserDashboard.jsx";
import AdminDashboard from "./components/AdminDashboard.jsx";
import Login from "./components/Login.jsx";
import Grooming from "./components/Grooming.jsx";
import PetList from "./components/PetList.jsx";
import Shop from "./components/Shop.jsx";
import Orders from "./components/Orders.jsx";

import {
  getAppointments,
  createAppointment,
  updateAppointment,
  deleteAppointment,
  getMe,
  getPets,
} from "./services/apiService.js";

function readStoredSession() {
  const token = localStorage.getItem("petivo_token");
  const savedUser = localStorage.getItem("petivo_user");
  let user = null;

  if (savedUser) {
    try {
      user = JSON.parse(savedUser);
    } catch {
      user = null;
    }
  }

  return {
    token,
    user,
    loggedIn: Boolean(token && user),
    role: user?.role ? user.role.toLowerCase() : "user",
  };
}

function App() {
  const [storedSession] = useState(readStoredSession);
  const [loggedIn, setLoggedIn] = useState(storedSession.loggedIn);
  const [role, setRole] = useState(storedSession.role);
  const [currentUser, setCurrentUser] = useState(storedSession.user);
  const [appointments, setAppointments] = useState([]);
  const [pets, setPets] = useState([]);
  const [error, setError] = useState("");
  const [activePage, setActivePage] = useState("Home");
  const [initialLoading, setInitialLoading] = useState(Boolean(storedSession.token));

  useEffect(() => {
    if (!storedSession.token) {
      return undefined;
    }

    let active = true;
    getMe()
      .then((res) => {
        if (!active || !res.data) return;
        setCurrentUser(res.data);
        setRole(res.data.role ? res.data.role.toLowerCase() : "user");
        setLoggedIn(true);
      })
      .catch(() => {
        if (!active) return;
        localStorage.removeItem("petivo_token");
        localStorage.removeItem("petivo_user");
        setLoggedIn(false);
        setCurrentUser(null);
      })
      .finally(() => {
        if (active) setInitialLoading(false);
      });

    return () => {
      active = false;
    };
  }, [storedSession.token]);

  useEffect(() => {
    const handleUnauthorized = () => {
      setLoggedIn(false);
      setRole("user");
      setCurrentUser(null);
      setAppointments([]);
      setPets([]);
      setError("Your session expired. Please sign in again.");
    };

    window.addEventListener("petivo:unauthorized", handleUnauthorized);
    return () => window.removeEventListener("petivo:unauthorized", handleUnauthorized);
  }, []);

  const loadUserPets = async () => {
    try {
      const res = await getPets();
      if (Array.isArray(res.data)) {
        setPets(res.data);
      } else {
        throw new Error("The pets API returned an invalid response.");
      }
    } catch {
      setError("Could not load your pets from the backend.");
    }
  };

  useEffect(() => {
    if (!loggedIn) return undefined;

    let active = true;
    getAppointments()
      .then((response) => {
        if (!Array.isArray(response.data)) {
          throw new Error("The appointments API returned an invalid response.");
        }
        if (active) {
          setAppointments(response.data);
          setError("");
        }
      })
      .catch(() => {
        if (active) {
          setError("Could not load appointments. Ensure the backend and database are running.");
        }
      });

    getPets()
      .then((response) => {
        if (!Array.isArray(response.data)) {
          throw new Error("The pets API returned an invalid response.");
        }
        if (active) setPets(response.data);
      })
      .catch(() => {
        if (active) setError("Could not load your pets from the backend.");
      });

    return () => {
      active = false;
    };
  }, [loggedIn]);

  const handleLogin = (loginRole, userObj) => {
    const normalizedRole = loginRole === "admin" || (userObj && userObj.role === "ADMIN") ? "admin" : "user";
    setRole(normalizedRole);
    if (userObj) {
      setCurrentUser(userObj);
    }
    setActivePage("Home");
    setLoggedIn(true);
  };

  const addAppointment = async (booking) => {
    try {
      const response = await createAppointment(booking);
      setAppointments((current) => [...current, response.data]);
      setError("");
      loadUserPets(); // Reload pets in case a new pet was auto-created
      return response.data;
    } catch (err) {
      setError("Could not save the appointment. Check the backend and booking details.");
      throw err;
    }
  };

  const editAppointment = async (id, updated) => {
    try {
      const response = await updateAppointment(id, updated);
      setAppointments((current) =>
        current.map((appointment) =>
          String(appointment.id) === String(id) ? response.data : appointment
        )
      );
      setError("");
    } catch {
      setError("Could not update the appointment. Check the backend.");
    }
  };

  const removeAppointment = async (id) => {
    if (!window.confirm("Delete this appointment?")) {
      return;
    }

    try {
      await deleteAppointment(id);
      setAppointments((current) =>
        current.filter((appointment) => String(appointment.id) !== String(id))
      );
      setError("");
    } catch {
      setError("Could not delete the appointment. Check the backend.");
    }
  };

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      const apt = appointments.find((a) => String(a.id) === String(id));
      if (!apt) return;
      const response = await updateAppointment(id, { ...apt, status: newStatus });
      setAppointments((current) =>
        current.map((a) => (String(a.id) === String(id) ? response.data : a))
      );
    } catch {
      setError("Could not update appointment status.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("petivo_token");
    localStorage.removeItem("petivo_user");
    setLoggedIn(false);
    setRole("user");
    setCurrentUser(null);
    setActivePage("Home");
    setAppointments([]);
    setPets([]);
    setError("");
  };

  if (initialLoading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="mt-3 text-muted">Loading PETIVO...</p>
        </div>
      </div>
    );
  }

  if (!loggedIn) {
    return <Login onLogin={handleLogin} initialError={error} />;
  }

  const renderPage = () => {
    if (activePage === "Grooming") {
      return <Grooming onBookService={addAppointment} />;
    }

    if (activePage === "My Pets") {
      return <PetList />;
    }

    if (activePage === "Pet Shop") {
      return <Shop />;
    }

    if (activePage === "Orders") {
      return <Orders />;
    }

    if (role === "admin") {
      return (
        <AdminDashboard
          appointments={appointments}
          onLogout={handleLogout}
          onUpdateStatus={handleStatusUpdate}
          onDeleteAppointment={removeAppointment}
        />
      );
    }

    return (
      <UserDashboard
        currentUser={currentUser}
        pets={pets}
        appointments={appointments}
        addAppointment={addAppointment}
        updateAppointment={editAppointment}
        deleteAppointment={removeAppointment}
      />
    );
  };

  return (
    <div className="petivo-app">
      <Navbar currentUser={currentUser} onLogout={handleLogout} />

      <div className="app-layout">
        <Sidebar activePage={activePage} onNavigate={setActivePage} />

        <main className="main-content">
          {error && (
            <div className="alert alert-warning alert-dismissible fade show" role="alert">
              {error}
              <button
                type="button"
                className="btn-close"
                onClick={() => setError("")}
              ></button>
            </div>
          )}

          {renderPage()}
        </main>
      </div>
    </div>
  );
}

export default App;
