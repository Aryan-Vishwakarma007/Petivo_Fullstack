import { useState } from "react";
import StatCards from "./StatCards";
import Appointment from "./Appointment";
import BookingModal from "./BookingModal";

function UserDashboard({
  currentUser,
  pets = [],
  appointments = [],
  addAppointment,
  updateAppointment,
  deleteAppointment,
}) {
  const [showBooking, setShowBooking] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState(null);

  const displayName = currentUser?.name || "Shreyash";

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Welcome back, {displayName}! 🐾</h2>
          <p className="text-muted">
            Here's what's happening with your pets today.
          </p>
        </div>

        <button
          className="btn btn-primary px-3 py-2 fw-semibold"
          onClick={() => setShowBooking(true)}
        >
          + Book an Appointment
        </button>
      </div>

      <StatCards
        stats={{
          pets: pets.length > 0 ? pets.length : 3,
          upcomingAppointments: appointments.length,
          activeOrders: 2,
          totalSpent: 0,
        }}
      />

      <Appointment
        appointments={appointments}
        onEdit={setEditingAppointment}
        onDelete={deleteAppointment}
      />

      {showBooking && (
        <BookingModal
          onClose={() => setShowBooking(false)}
          onBook={addAppointment}
        />
      )}

      {editingAppointment && (
        <BookingModal
          isEditing
          initialData={editingAppointment}
          onClose={() => setEditingAppointment(null)}
          onBook={async (updated) => {
            await updateAppointment(editingAppointment.id, updated);
            setEditingAppointment(null);
          }}
        />
      )}
    </div>
  );
}

export default UserDashboard;
