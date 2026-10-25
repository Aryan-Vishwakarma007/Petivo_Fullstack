function Appointment({ appointments, onEdit, onDelete }) {
  return (
    <div className="card border-0 shadow-sm">
      <div className="card-body">
        <h5 className="fw-bold mb-3">📅 Appointments</h5>

        <div className="table-responsive">
          <table className="table align-middle">
            <thead>
              <tr>
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
                  <td className="fw-semibold">🐾 {a.pet}</td>
                  <td>{a.owner}</td>
                  <td>{a.service}</td>
                  <td>{a.date}</td>
                  <td>
                    <span
                      className={`badge ${
                        a.status === "Confirmed"
                          ? "bg-success"
                          : "bg-warning text-dark"
                      }`}
                    >
                      {a.status}
                    </span>
                  </td>
                  <td>
                    <button
                      className="btn btn-sm btn-outline-primary me-2"
                      onClick={() => onEdit(a)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => onDelete(a.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}

              {appointments.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center text-muted py-4">
                    No appointments found. Create one using "Book an Appointment".
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Appointment;
