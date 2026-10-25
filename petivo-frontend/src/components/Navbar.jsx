function Navbar({ currentUser, onLogout }) {
  const displayName = currentUser?.name || "Shreyash";
  const displayRole = currentUser?.role === "ADMIN" ? "Administrator" : "Pet Parent";
  const initial = displayName ? displayName.charAt(0).toUpperCase() : "P";

  return (
    <nav className="navbar">
      <div className="logo">🐾 PETIVO</div>

      <div className="nav-right">
        <button className="icon-btn" title="Notifications">
          <i className="bi bi-bell"></i>
        </button>

        <button className="icon-btn" title="Cart">
          <i className="bi bi-cart3"></i>
        </button>

        <div className="profile">
          <div className="avatar">{initial}</div>
          <div>
            <h6>{displayName}</h6>
            <small>{displayRole}</small>
          </div>
        </div>

        <button className="btn btn-outline-danger btn-sm ms-2" onClick={onLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
