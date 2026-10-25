import { useState } from "react";

const emptyForm = {
  pet: "",
  owner: "",
  service: "",
  date: "",
  status: "Pending",
};

function BookingModal({ onClose, onBook, initialData = null, isEditing = false }) {
  const [formData, setFormData] = useState(initialData || emptyForm);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await onBook({ ...formData, status: formData.status || "Pending" });
    onClose();
  };

  return (
    <div className="modal d-block" style={{ backgroundColor: "rgba(0,0,0,0.5)" }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">
              🐾 {isEditing ? "Edit Appointment" : "Book an Appointment"}
            </h5>
            <button type="button" className="btn-close" onClick={onClose}></button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="modal-body">
              <div className="mb-3">
                <label className="form-label">Pet Name</label>
                <input
                  type="text"
                  name="pet"
                  className="form-control"
                  value={formData.pet}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Owner Name</label>
                <input
                  type="text"
                  name="owner"
                  className="form-control"
                  value={formData.owner}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Service</label>
                <select
                  name="service"
                  className="form-select"
                  value={formData.service}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a service</option>
                  <option>Full Grooming</option>
                  <option>Bath & Blow Dry</option>
                  <option>Veterinary Checkup</option>
                  <option>Nail Trimming</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label">Appointment Date</label>
                <input
                  type="date"
                  name="date"
                  className="form-control"
                  value={formData.date}
                  onChange={handleChange}
                  required
                />
              </div>

              {isEditing && (
                <div className="mb-3">
                  <label className="form-label">Status</label>
                  <select
                    name="status"
                    className="form-select"
                    value={formData.status}
                    onChange={handleChange}
                  >
                    <option>Pending</option>
                    <option>Confirmed</option>
                  </select>
                </div>
              )}
            </div>

            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" onClick={onClose}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                {isEditing ? "Save Changes" : "Book Appointment"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default BookingModal;
