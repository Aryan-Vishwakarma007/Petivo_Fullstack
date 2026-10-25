function StatCards({ stats }) {
  return (
    <div className="row g-3 mb-4">
      <div className="col-md-3">
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <p className="text-muted mb-1">My Pets</p>
            <h3>{stats.pets}</h3>
            <span>🐾 Pets</span>
          </div>
        </div>
      </div>

      <div className="col-md-3">
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <p className="text-muted mb-1">Appointments</p>
            <h3>{stats.upcomingAppointments}</h3>
            <span>📅 Upcoming</span>
          </div>
        </div>
      </div>

      <div className="col-md-3">
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <p className="text-muted mb-1">Active Orders</p>
            <h3>{stats.activeOrders}</h3>
            <span>🛍️ Orders</span>
          </div>
        </div>
      </div>

      <div className="col-md-3">
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <p className="text-muted mb-1">Total Spent</p>
            <h3>₹{stats.totalSpent}</h3>
            <span>💳 Spending</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StatCards;
