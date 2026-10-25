import { useState, useEffect } from "react";
import { getAdminStats, getAllUsers } from "../services/apiService";

function AdminDashboard({ appointments = [], onLogout, onUpdateStatus, onDeleteAppointment }) {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [statsError, setStatsError] = useState("");
  const [usersError, setUsersError] = useState("");
  const [viewTab, setViewTab] = useState("appointments");

  useEffect(() => {
    getAdminStats()
      .then((res) => {
        setStats(res.data);
        setStatsError("");
      })
      .catch(() => setStatsError("Unable to load live appointment statistics."));

    getAllUsers()
      .then((res) => {
        if (!Array.isArray(res.data)) {
          throw new Error("The users API returned an invalid response.");
        }
        setUsers(res.data);
        setUsersError("");
      })
      .catch(() => setUsersError("Unable to load the registered user list."));
  }, [appointments]);

  const totalCount = stats ? stats.totalAppointments : appointments.length;
  const confirmedCount = stats ? stats.confirmedAppointments : appointments.filter((a) => a.status === "Confirmed").length;
  const pendingCount = stats ? stats.pendingAppointments : appointments.filter((a) => a.status === "Pending").length;
  const totalUsersCount = usersError ? "Unavailable" : stats ? stats.totalUsers : users.length;

  return (
    <div className="container-fluid p-2">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Admin Management Console ⚙️</h2>
          <p className="text-muted mb-0">System-wide monitoring of appointments, users, and services.</p>
        </div>
        {onLogout && (
          <button className="btn btn-outline-danger" onClick={onLogout}>
            Logout
          </button>
        )}
      </div>

      {statsError && <div className="alert alert-warning" role="status">{statsError}</div>}
      {usersError && <div className="alert alert-warning" role="status">{usersError}</div>}

      {/* Real Database Statistics Cards */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm rounded-4">
            <div className="card-body">
              <span className="text-muted small fw-semibold">TOTAL APPOINTMENTS</span>
              <h2 className="fw-bold text-primary mt-2">{totalCount}</h2>
              <span className="small text-secondary">In MySQL database</span>
            </div>
          </div>
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm rounded-4">
            <div className="card-body">
              <span className="text-muted small fw-semibold">CONFIRMED</span>
              <h2 className="fw-bold text-success mt-2">{confirmedCount}</h2>
              <span className="small text-secondary">Active bookings</span>
            </div>
          </div>
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm rounded-4">
            <div className="card-body">
              <span className="text-muted small fw-semibold">PENDING</span>
              <h2 className="fw-bold text-warning mt-2">{pendingCount}</h2>
              <span className="small text-secondary">Awaiting confirmation</span>
            </div>
          </div>
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 shadow-sm rounded-4">
            <div className="card-body">
              <span className="text-muted small fw-semibold">REGISTERED USERS</span>
              <h2 className="fw-bold text-info mt-2">{totalUsersCount}</h2>
              <span className="small text-secondary">Pet parents & admins</span>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <ul className="nav nav-pills mb-3">
        <li className="nav-item">
          <button
            className={`nav-link ${viewTab === "appointments" ? "active" : ""}`}
            onClick={() => setViewTab("appointments")}
          >
            All Appointments ({appointments.length})
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${viewTab === "users" ? "active" : ""}`}
            onClick={() => setViewTab("users")}
          >
            User Accounts ({users.length})
          </button>
        </li>
      </ul>

      {viewTab === "appointments" ? (
        <div className="card border-0 shadow-sm rounded-4">
          <div className="card-body p-4">
            <h5 className="fw-bold mb-3">Appointments Ledger</h5>
            <div className="table-responsive">
              <table className="table align-middle">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Pet</th>
                    <th>Owner</th>
                    <th>Service</th>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((a) => (
                    <tr key={a.id}>
                      <td className="text-muted small">#{a.id}</td>
                      <td className="fw-semibold">🐾 {a.pet}</td>
                      <td>{a.owner}</td>
                      <td>{a.service}</td>
                      <td>{a.date}</td>
                      <td>
                        <span
                          className={`badge ${
                            a.status === "Confirmed"
                              ? "bg-success"
                              : a.status === "Cancelled"
                              ? "bg-danger"
                              : "bg-warning text-dark"
                          }`}
                        >
                          {a.status}
                        </span>
                      </td>
                      <td>
                        {onUpdateStatus && (
                          <button
                            className={`btn btn-sm ${
                              a.status === "Confirmed" ? "btn-outline-warning" : "btn-outline-success"
                            } me-2`}
                            onClick={() =>
                              onUpdateStatus(
                                a.id,
                                a.status === "Confirmed" ? "Pending" : "Confirmed"
                              )
                            }
                          >
                            {a.status === "Confirmed" ? "Mark Pending" : "Confirm"}
                          </button>
                        )}
                        {onDeleteAppointment && (
                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => onDeleteAppointment(a.id)}
                          >
                            Delete
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                  {appointments.length === 0 && (
                    <tr>
                      <td colSpan="7" className="text-center text-muted py-4">
                        No appointment records in the database.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      ) : (
        <div className="card border-0 shadow-sm rounded-4">
          <div className="card-body p-4">
            <h5 className="fw-bold mb-3">Registered Users</h5>
            <div className="table-responsive">
              <table className="table align-middle">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Created At</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id}>
                      <td className="text-muted small">#{u.id}</td>
                      <td className="fw-semibold">{u.name}</td>
                      <td>{u.email}</td>
                      <td>
                        <span
                          className={`badge ${
                            u.role === "ADMIN" ? "bg-danger" : "bg-primary"
                          }`}
                        >
                          {u.role}
                        </span>
                      </td>
                      <td className="small text-muted">{u.createdAt || "N/A"}</td>
                    </tr>
                  ))}
                  {users.length === 0 && (
                    <tr>
                      <td colSpan="5" className="text-center text-muted py-4">
                        No users loaded.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminDashboard;
