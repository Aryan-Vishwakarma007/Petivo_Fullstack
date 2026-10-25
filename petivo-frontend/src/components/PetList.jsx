import { useState, useEffect } from "react";
import { getPets, createPet, updatePet, deletePet } from "../services/apiService";

function PetList() {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editingPet, setEditingPet] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    type: "Dog",
    breed: "",
    age: "",
  });

  const loadPets = async () => {
    try {
      setLoading(true);
      const res = await getPets();
      setPets(Array.isArray(res.data) ? res.data : []);
      setError("");
    } catch {
      setError("Could not load pets from database.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let active = true;
    getPets()
      .then((res) => {
        if (!Array.isArray(res.data)) {
          throw new Error("The pets API returned an invalid response.");
        }
        if (active) {
          setPets(res.data);
          setError("");
        }
      })
      .catch(() => {
        if (active) setError("Could not load pets from database.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const openAddModal = () => {
    setEditingPet(null);
    setFormData({ name: "", type: "Dog", breed: "", age: "" });
    setShowModal(true);
  };

  const openEditModal = (pet) => {
    setEditingPet(pet);
    setFormData({
      name: pet.name,
      type: pet.type,
      breed: pet.breed || "",
      age: pet.age || "",
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        name: formData.name.trim(),
        type: formData.type.trim(),
        breed: formData.breed.trim() || null,
        age: formData.age ? parseInt(formData.age, 10) : null,
      };

      if (editingPet) {
        await updatePet(editingPet.id, payload);
      } else {
        await createPet(payload);
      }

      setShowModal(false);
      loadPets();
    } catch {
      setError("Failed to save the pet. Please check the backend connection and details.");
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to remove ${name}?`)) {
      return;
    }
    try {
      await deletePet(id);
      setPets((prev) => prev.filter((p) => p.id !== id));
    } catch {
      setError("Failed to delete the pet.");
    }
  };

  const getPetIcon = (type) => {
    const t = (type || "").toLowerCase();
    if (t.includes("dog")) return "🐶";
    if (t.includes("cat")) return "🐱";
    if (t.includes("bird")) return "🦜";
    if (t.includes("fish")) return "🐠";
    return "🐾";
  };

  return (
    <div className="container-fluid py-2">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">My Pets 🐾</h2>
          <p className="text-muted mb-0">Manage your beloved companions and their profiles.</p>
        </div>
        <button className="btn btn-primary px-3 py-2 fw-semibold" onClick={openAddModal}>
          + Add New Pet
        </button>
      </div>

      {error && <div className="alert alert-warning">{error}</div>}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading pets...</span>
          </div>
        </div>
      ) : pets.length === 0 ? (
        <div className="card border-0 shadow-sm text-center p-5">
          <div className="fs-1 mb-3">🐾</div>
          <h4 className="fw-bold">No pets registered yet</h4>
          <p className="text-muted">Add your pets to book appointments and manage grooming faster!</p>
          <div className="mt-2">
            <button className="btn btn-primary" onClick={openAddModal}>
              Register Your First Pet
            </button>
          </div>
        </div>
      ) : (
        <div className="row g-4">
          {pets.map((pet) => (
            <div className="col-12 col-md-6 col-lg-4" key={pet.id}>
              <div className="card border-0 shadow-sm rounded-4 h-100">
                <div className="card-body p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <span className="fs-1">{getPetIcon(pet.type)}</span>
                    <span className="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 rounded-pill">
                      {pet.type}
                    </span>
                  </div>

                  <h4 className="fw-bold mb-1">{pet.name}</h4>
                  <p className="text-muted small mb-3">
                    {pet.breed ? `${pet.breed}` : "Breed not specified"}
                    {pet.age ? ` • ${pet.age} years old` : ""}
                  </p>

                  <div className="mt-auto d-flex gap-2 pt-2 border-top">
                    <button
                      className="btn btn-sm btn-outline-primary flex-fill"
                      onClick={() => openEditModal(pet)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => handleDelete(pet.id, pet.name)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit Modal */}
      {showModal && (
        <div className="modal d-block" style={{ backgroundColor: "rgba(0,0,0,0.5)" }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">
                  🐾 {editingPet ? "Edit Pet Details" : "Register New Pet"}
                </h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="modal-body p-4">
                  <div className="mb-3">
                    <label className="form-label fw-semibold">Pet Name *</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      placeholder="e.g. Bruno"
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold">Species / Type *</label>
                    <select
                      className="form-select"
                      value={formData.type}
                      onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                    >
                      <option value="Dog">Dog</option>
                      <option value="Cat">Cat</option>
                      <option value="Bird">Bird</option>
                      <option value="Rabbit">Rabbit</option>
                      <option value="Other">Other</option>
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold">Breed</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.breed}
                      onChange={(e) => setFormData({ ...formData, breed: e.target.value })}
                      placeholder="e.g. Golden Retriever"
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold">Age (Years)</label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.age}
                      onChange={(e) => setFormData({ ...formData, age: e.target.value })}
                      min="0"
                      max="30"
                      placeholder="e.g. 3"
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary">
                    {editingPet ? "Save Changes" : "Register Pet"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PetList;
