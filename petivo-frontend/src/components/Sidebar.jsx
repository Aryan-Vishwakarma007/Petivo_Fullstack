function Sidebar({ activePage, onNavigate }) {
  const menuItems = [
    { label: "Home", page: "Home", icon: "bi-house-door-fill" },
    { label: "Grooming", page: "Grooming", icon: "bi-heart-pulse-fill" },
    {
      label: "Appointments",
      page: "Appointments",
      icon: "bi-calendar-check-fill",
    },
    { label: "My Pets", page: "My Pets", icon: "bi-emoji-smile-fill" },
    { label: "Pet Shop", page: "Pet Shop", icon: "bi-bag-fill" },
    { label: "Orders", page: "Orders", icon: "bi-receipt-cutoff" },
  ];

  return (
    <aside className="sidebar">
      <h4 className="mb-4">Dashboard</h4>

      <ul>
        {menuItems.map((item) => (
          <li key={item.page}>
            <button
              type="button"
              onClick={() => onNavigate(item.page)}
              className={`sidebar-button ${
                activePage === item.page ? "active" : ""
              }`}
            >
              <i className={`bi ${item.icon}`} /> {item.label}
            </button>
          </li>
        ))}
      </ul>
    </aside>
  );
}

export default Sidebar;
