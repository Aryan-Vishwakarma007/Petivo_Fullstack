import { useState } from "react";

const shopProducts = [
  { id: 1, name: "Premium Dog Kibble (3kg)", price: 1450, category: "Food", rating: "4.8 ★", icon: "🍖" },
  { id: 2, name: "Organic Catnip Treats", price: 350, category: "Treats", rating: "4.9 ★", icon: "🐟" },
  { id: 3, name: "Orthopedic Memory Foam Pet Bed", price: 2899, category: "Comfort", rating: "4.9 ★", icon: "🛏️" },
  { id: 4, name: "Stainless Steel Anti-Gulp Bowl", price: 650, category: "Feeding", rating: "4.7 ★", icon: "🥣" },
  { id: 5, name: "Natural Herbal Flea & Tick Shampoo", price: 520, category: "Care", rating: "4.6 ★", icon: "🧴" },
  { id: 6, name: "Interactive Squeaky Ball Toy", price: 299, category: "Toys", rating: "4.8 ★", icon: "🎾" },
];

function Shop() {
  const [cart, setCart] = useState([]);
  const [notification, setNotification] = useState("");

  const addToCart = (product) => {
    setCart((prev) => [...prev, product]);
    setNotification(`Added "${product.name}" to cart!`);
    setTimeout(() => setNotification(""), 3000);
  };

  return (
    <div className="container-fluid py-2">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">PETIVO Pet Shop 🛍️</h2>
          <p className="text-muted mb-0">High-quality food, treats, toys, and healthcare supplies for your pets.</p>
        </div>
        <span className="badge bg-primary px-3 py-2 rounded-pill fs-6">
          🛒 Cart: {cart.length} items
        </span>
      </div>

      {notification && (
        <div className="alert alert-success alert-dismissible fade show" role="alert">
          {notification}
          <button type="button" className="btn-close" onClick={() => setNotification("")}></button>
        </div>
      )}

      <div className="row g-4">
        {shopProducts.map((p) => (
          <div className="col-12 col-md-6 col-lg-4" key={p.id}>
            <div className="card h-100 border-0 shadow-sm rounded-4">
              <div className="card-body p-4 d-flex flex-column">
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <span className="fs-1">{p.icon}</span>
                  <span className="badge bg-light text-dark">{p.category}</span>
                </div>
                <h5 className="fw-bold mb-1">{p.name}</h5>
                <span className="text-warning small mb-3">{p.rating} Verified</span>

                <div className="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                  <span className="fs-5 fw-bold text-primary">₹{p.price}</span>
                  <button className="btn btn-primary btn-sm px-3" onClick={() => addToCart(p)}>
                    + Add to Cart
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Shop;
