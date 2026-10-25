import { useState, useEffect } from "react";
import { getServices, getPets } from "../services/apiService";

function Grooming({ onBookService }) {
  const [services, setServices] = useState([]);
  const [userPets, setUserPets] = useState([]);
  const [servicesLoading, setServicesLoading] = useState(true);
  const [servicesError, setServicesError] = useState("");
  const [petsError, setPetsError] = useState("");
  const [selectedService, setSelectedService] = useState(null);
  const [petName, setPetName] = useState("");
  const [petType, setPetType] = useState("Dog");
  const [bookingDate, setBookingDate] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const today = new Date().toLocaleDateString("en-CA");

  useEffect(() => {
    getServices()
      .then((res) => {
        if (!Array.isArray(res.data)) {
          throw new Error("The services API returned an invalid response.");
        }
        setServices(res.data.map((service, index) => ({
          ...service,
          duration: service.duration || "45 minutes",
          icon: service.icon || (index % 4 === 0 ? "🛁" : index % 4 === 1 ? "✂️" : index % 4 === 2 ? "🐾" : "🐶"),
        })));
      })
      .catch(() => {
        setServicesError("Could not load services from the backend.");
      })
      .finally(() => {
        setServicesLoading(false);
      });

    getPets()
      .then((res) => {
        if (!Array.isArray(res.data)) {
          throw new Error("The pets API returned an invalid response.");
        }
        setUserPets(res.data);
        if (res.data.length > 0) {
          setPetName(res.data[0].name);
          setPetType(res.data[0].type);
        }
      })
      .catch(() => {
        setPetsError("Could not load your pets. You can enter a pet name manually.");
      });
  }, []);

  const handleBooking = async (event) => {
    event.preventDefault();

    if (!selectedService || !petName.trim() || !bookingDate) {
      setMessage("Please complete all booking details.");
      return;
    }

    setLoading(true);
    const booking = {
      pet: petName.trim(),
      owner: "PETIVO User",
      service: selectedService.name,
      date: bookingDate,
      status: "Pending",
      petType,
    };

    try {
      if (typeof onBookService !== "function") {
        throw new Error("Appointment booking is not connected to the backend.");
      }
      await onBookService(booking);
      setMessage("Booking saved. Check Appointments for its status.");
      setSelectedService(null);
      setBookingDate("");
    } catch {
      setMessage("Could not save appointment to database. Please check backend connection.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-2">
      <div className="p-4 p-md-5 mb-4 rounded-4 bg-light shadow-sm">
        <h1 className="fw-bold">Pet Grooming & Services ✨</h1>
        <p className="text-secondary mb-0">
          Professional care, bathing, and health checkups for your furry family members.
        </p>
      </div>

      {message && (
        <div className="alert alert-info alert-dismissible fade show mb-4" role="status">
          {message}
          <button type="button" className="btn-close" onClick={() => setMessage("")}></button>
        </div>
      )}

      {servicesError && <div className="alert alert-danger" role="alert">{servicesError}</div>}
      {petsError && <div className="alert alert-warning" role="alert">{petsError}</div>}
      {servicesLoading ? (
        <div className="text-center py-5" role="status">Loading services...</div>
      ) : services.length === 0 ? (
        <div className="alert alert-info">No services are currently available.</div>
      ) : <div className="row g-4">
        {services.map((service) => (
          <div className="col-12 col-md-6 col-xl-4" key={service.id}>
            <div className="card h-100 border-0 shadow-sm rounded-4">
              <div className="card-body p-4 d-flex flex-column">
                <div className="fs-1 mb-3">{service.icon || "🐾"}</div>

                <h5 className="fw-bold">{service.name}</h5>
                <p className="text-secondary small">{service.description}</p>

                <div className="mt-auto pt-3 border-top">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <span className="fs-5 fw-bold text-primary">₹{service.price}</span>
                    <span className="badge bg-light text-dark">{service.duration}</span>
                  </div>

                  <button
                    className="btn btn-primary w-100"
                    onClick={() => {
                      setSelectedService(service);
                      setMessage("");
                    }}
                  >
                    Book Service
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>}

      {selectedService && (
        <div className="card border-0 shadow-sm rounded-4 mt-5">
          <div className="card-body p-4">
            <h4 className="fw-bold mb-3">📅 Book {selectedService.name}</h4>

            <form onSubmit={handleBooking}>
              <div className="row g-3">
                <div className="col-12 col-md-4">
                  <label className="form-label fw-semibold">Pet Name</label>
                  {userPets.length > 0 ? (
                    <div className="input-group">
                      <select
                        className="form-select"
                        value={petName}
                        onChange={(e) => {
                          const val = e.target.value;
                          setPetName(val);
                          const matched = userPets.find((p) => p.name === val);
                          if (matched) setPetType(matched.type);
                        }}
                      >
                        {userPets.map((p) => (
                          <option key={p.id} value={p.name}>
                            {p.name} ({p.type})
                          </option>
                        ))}
                        <option value="__other__">+ Enter a new pet name</option>
                      </select>
                      {petName === "__other__" && (
                        <input
                          type="text"
                          className="form-control mt-2"
                          placeholder="Enter pet name"
                          onChange={(e) => setPetName(e.target.value)}
                          required
                        />
                      )}
                    </div>
                  ) : (
                    <input
                      className="form-control"
                      value={petName}
                      onChange={(event) => setPetName(event.target.value)}
                      placeholder="Enter pet name (e.g. Bruno)"
                      required
                    />
                  )}
                </div>

                <div className="col-12 col-md-4">
                  <label className="form-label fw-semibold">Pet Type</label>
                  <select
                    className="form-select"
                    value={petType}
                    onChange={(event) => setPetType(event.target.value)}
                  >
                    <option value="Dog">Dog</option>
                    <option value="Cat">Cat</option>
                    <option value="Bird">Bird</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div className="col-12 col-md-4">
                  <label className="form-label fw-semibold">Preferred Date</label>
                  <input
                    type="date"
                    className="form-control"
                    min={today}
                    value={bookingDate}
                    onChange={(event) => setBookingDate(event.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="p-3 bg-light rounded-3 mt-3 d-flex justify-content-between align-items-center">
                <span>
                  Selected service: <strong>{selectedService.name}</strong> • Total: <strong className="text-primary">₹{selectedService.price}</strong>
                </span>
                <div>
                  <button className="btn btn-outline-secondary me-2" type="button" onClick={() => setSelectedService(null)}>
                    Cancel
                  </button>
                  <button className="btn btn-success" type="submit" disabled={loading}>
                    {loading ? "Submitting..." : "Confirm Booking"}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Grooming;
