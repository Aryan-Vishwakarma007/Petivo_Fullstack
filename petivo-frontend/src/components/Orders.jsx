function Orders() {
  const sampleOrders = [
    { id: "ORD-9821", date: "2026-10-12", items: "Premium Dog Kibble (3kg)", total: "₹1,450", status: "Delivered", badge: "bg-success" },
    { id: "ORD-9810", date: "2026-10-05", items: "Organic Catnip Treats + Herbal Shampoo", total: "₹870", status: "Delivered", badge: "bg-success" },
    { id: "ORD-9835", date: "2026-10-14", items: "Orthopedic Memory Foam Pet Bed", total: "₹2,899", status: "In Transit", badge: "bg-primary" },
  ];

  return (
    <div className="container-fluid py-2">
      <div className="mb-4">
        <h2 className="fw-bold">My Orders 📦</h2>
        <p className="text-muted mb-0">Track your recent pet supply orders and purchases.</p>
      </div>

      <div className="card border-0 shadow-sm rounded-4">
        <div className="card-body p-4">
          <div className="table-responsive">
            <table className="table align-middle">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>Items Purchased</th>
                  <th>Total Amount</th>
                  <th>Delivery Status</th>
                </tr>
              </thead>
              <tbody>
                {sampleOrders.map((o) => (
                  <tr key={o.id}>
                    <td className="fw-semibold text-primary">{o.id}</td>
                    <td>{o.date}</td>
                    <td>{o.items}</td>
                    <td className="fw-semibold">{o.total}</td>
                    <td>
                      <span className={`badge ${o.badge}`}>{o.status}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Orders;
