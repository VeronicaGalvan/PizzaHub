import React, { useState, useEffect } from "react";

const EntradaPedidos = () => {
  const clienteId = Number(localStorage.getItem("pedidoClienteId"));
  const token = localStorage.getItem("token");

  const [productos, setProductos] = useState([]);
  const [detalles, setDetalles] = useState([]);

  const [form, setForm] = useState({
    tipo: 1,
    metodoPago: 1,
    origen: 1,
    direccionEntrega: "",
    observaciones: ""
  });

  // CARGAR PRODUCTOS
  const fetchProductos = async () => {
    try {
      const res = await fetch("https://pizzahub-api.onrender.com/api/Productos", {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (!res.ok) return;

      const data = await res.json();
      setProductos(data);
    } catch (e) {
      console.log("Error productos:", e);
    }
  };

  useEffect(() => {
    fetchProductos();
  }, []);

  // AÑADIR PRODUCTO
  const agregarProducto = (productoId) => {
    const existe = detalles.find((d) => d.productoId === productoId);
    if (existe) {
      setDetalles(
        detalles.map((d) =>
          d.productoId === productoId
            ? { ...d, cantidad: d.cantidad + 1 }
            : d
        )
      );
    } else {
      setDetalles([...detalles, { productoId, cantidad: 1 }]);
    }
  };

  // QUITAR PRODUCTO
  const quitarProducto = (productoId) => {
    const existe = detalles.find((d) => d.productoId === productoId);
    if (existe && existe.cantidad > 1) {
      setDetalles(
        detalles.map((d) =>
          d.productoId === productoId
            ? { ...d, cantidad: d.cantidad - 1 }
            : d
        )
      );
    } else {
      setDetalles(detalles.filter((d) => d.productoId !== productoId));
    }
  };

  // TOTAL
  const calcularTotal = () =>
    detalles.reduce((sum, d) => {
      const prod = productos.find((p) => p.id === d.productoId);
      return sum + (prod?.precio || 0) * d.cantidad;
    }, 0);

  // REGISTRAR PEDIDO
  const registrarPedido = async () => {
    if (!clienteId) return alert("❌ No se seleccionó un cliente.");
    if (detalles.length === 0)
      return alert("❌ Agrega al menos un producto.");

    const payload = {
      clienteId,
      ...form,
      detalles
    };

    try {
      const res = await fetch(
        "https://pizzahub-api.onrender.com/api/PedidosNew/registrar",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        }
      );

      if (!res.ok) {
        alert("Error al registrar pedido ❌");
        return;
      }

      alert("Pedido registrado con éxito ✔");
      localStorage.removeItem("pedidoClienteId");
      window.location.href = "/pedidos";
    } catch (e) {
      console.log("Error:", e);
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)",
        padding: "30px 20px"
      }}
    >
      <div style={{ maxWidth: "1400px", margin: "0 auto" }}>
        {/* HEADER */}
        <div
          style={{
            background: "white",
            borderRadius: "16px",
            padding: "25px 30px",
            marginBottom: "30px",
            boxShadow: "0 4px 20px rgba(0, 0, 0, 0.08)"
          }}
        >
          <h1
            style={{
              margin: 0,
              fontSize: "32px",
              fontWeight: "800",
              background: "linear-gradient(135deg, #ff8c00 0%, #ff4500 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent"
            }}
          >
            🛒 Registrar Nuevo Pedido
          </h1>

          <div
            style={{
              marginTop: "12px",
              display: "flex",
              alignItems: "center",
              gap: "10px"
            }}
          >
            <span style={{ color: "#666", fontSize: "14px" }}>
              Cliente ID:
            </span>

            <span
              style={{
                background: "linear-gradient(135deg, #ff8c00 0%, #ff4500 100%)",
                color: "white",
                padding: "4px 16px",
                borderRadius: "20px",
                fontWeight: "700",
                fontSize: "14px"
              }}
            >
              #{clienteId || "No seleccionado"}
            </span>
          </div>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 400px",
            gap: "30px"
          }}
        >
          {/* IZQUIERDA: PRODUCTOS */}
          <div>
            <div
              style={{
                background: "white",
                borderRadius: "16px",
                padding: "24px",
                boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                marginBottom: "20px"
              }}
            >
              <h3
                style={{
                  margin: 0,
                  marginBottom: "20px",
                  fontSize: "20px",
                  fontWeight: "700",
                  display: "flex",
                  alignItems: "center",
                  gap: "10px"
                }}
              >
                🍕 Menú de Productos
              </h3>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns:
                    "repeat(auto-fill, minmax(200px, 1fr))",
                  gap: "16px",
                  maxHeight: "calc(100vh - 300px)",
                  overflowY: "auto",
                  paddingRight: "10px"
                }}
              >
                {productos.map((p) => (
                  <div
                    key={p.id}
                    style={{
                      background: "#f8f9fa",
                      borderRadius: "12px",
                      padding: "16px",
                      border: "2px solid #e0e0e0",
                      cursor: "pointer",
                      transition: "all 0.2s ease"
                    }}
                  >
                    <div
                      style={{
                        fontWeight: "700",
                        fontSize: "16px",
                        marginBottom: "8px"
                      }}
                    >
                      {p.nombre}
                    </div>

                    <div
                      style={{
                        fontSize: "20px",
                        fontWeight: "800",
                        color: "#ff8c00",
                        marginBottom: "12px"
                      }}
                    >
                      ${p.precio}
                    </div>

                    <button
                      onClick={() => agregarProducto(p.id)}
                      style={{
                        width: "100%",
                        padding: "10px",
                        background:
                          "linear-gradient(135deg, #ff8c00 0%, #ff4500 100%)",
                        border: "none",
                        borderRadius: "8px",
                        color: "white",
                        fontWeight: "600",
                        fontSize: "14px",
                        cursor: "pointer"
                      }}
                    >
                      + Agregar
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* DERECHA: FORM + RESUMEN */}
          <div>
            {/* FORMULARIO */}
            <div
              style={{
                background: "white",
                borderRadius: "16px",
                padding: "24px",
                boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                marginBottom: "20px"
              }}
            >
              <h3
                style={{
                  margin: 0,
                  marginBottom: "20px",
                  fontSize: "18px",
                  fontWeight: "700"
                }}
              >
                📋 Información del Pedido
              </h3>

              {/* DIRECCIÓN */}
              <div style={{ marginBottom: "16px" }}>
                <label
                  style={{
                    fontSize: "12px",
                    fontWeight: "700",
                    marginBottom: "8px",
                    display: "block"
                  }}
                >
                  📍 Dirección de entrega
                </label>

                <input
                  type="text"
                  value={form.direccionEntrega}
                  onChange={(e) =>
                    setForm({ ...form, direccionEntrega: e.target.value })
                  }
                  placeholder="Ingresa la dirección..."
                  style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "2px solid #e0e0e0"
                  }}
                />
              </div>

              {/* OBSERVACIONES */}
              <div style={{ marginBottom: "16px" }}>
                <label
                  style={{
                    fontSize: "12px",
                    fontWeight: "700",
                    marginBottom: "8px",
                    display: "block"
                  }}
                >
                  📝 Observaciones
                </label>

                <textarea
                  rows="3"
                  value={form.observaciones}
                  onChange={(e) =>
                    setForm({ ...form, observaciones: e.target.value })
                  }
                  placeholder="Notas adicionales..."
                  style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "2px solid #e0e0e0",
                    resize: "vertical"
                  }}
                />
              </div>

              {/* TIPO */}
              <div style={{ marginBottom: "16px" }}>
                <label
                  style={{
                    fontSize: "12px",
                    fontWeight: "700",
                    marginBottom: "8px",
                    display: "block"
                  }}
                >
                  🏠 Tipo de pedido
                </label>

                <select
                  value={form.tipo}
                  onChange={(e) =>
                    setForm({ ...form, tipo: Number(e.target.value) })
                  }
                  style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "2px solid #e0e0e0"
                  }}
                >
                  <option value={1}>🚚 Domicilio</option>
                  <option value={2}>🏪 Mostrador</option>
                </select>
              </div>

              {/* MÉTODO DE PAGO */}
              <div style={{ marginBottom: "16px" }}>
                <label
                  style={{
                    fontSize: "12px",
                    fontWeight: "700",
                    marginBottom: "8px",
                    display: "block"
                  }}
                >
                  💳 Método de pago
                </label>

                <select
                  value={form.metodoPago}
                  onChange={(e) =>
                    setForm({ ...form, metodoPago: Number(e.target.value) })
                  }
                  style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "2px solid #e0e0e0"
                  }}
                >
                  <option value={1}>💵 Efectivo</option>
                  <option value={2}>💳 Tarjeta</option>
                </select>
              </div>
            </div>

            {/* RESUMEN */}
            <div
              style={{
                background: "white",
                borderRadius: "16px",
                padding: "24px",
                boxShadow: "0 4px 20px rgba(0,0,0,0.08)"
              }}
            >
              <h3
                style={{
                  margin: 0,
                  marginBottom: "20px",
                  fontSize: "18px",
                  fontWeight: "700"
                }}
              >
                🧾 Resumen del pedido
              </h3>

              {detalles.length === 0 ? (
                <div style={{ textAlign: "center", padding: "30px 20px" }}>
                  <div style={{ fontSize: "48px", marginBottom: "12px" }}>
                    🛒
                  </div>
                  <p style={{ margin: 0 }}>Aún no has agregado productos</p>
                </div>
              ) : (
                <>
                  <div
                    style={{
                      maxHeight: "300px",
                      overflowY: "auto",
                      marginBottom: "20px"
                    }}
                  >
                    {detalles.map((d) => {
                      const prod = productos.find(
                        (p) => p.id === d.productoId
                      );

                      return (
                        <div
                          key={d.productoId}
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            padding: "12px",
                            background: "#f8f9fa",
                            borderRadius: "8px",
                            marginBottom: "8px"
                          }}
                        >
                          <div style={{ flex: 1 }}>
                            <div
                              style={{
                                fontWeight: "600",
                                fontSize: "14px"
                              }}
                            >
                              {prod?.nombre}
                            </div>
                            <div
                              style={{
                                fontSize: "12px",
                                color: "#ff8c00"
                              }}
                            >
                              ${prod?.precio} c/u
                            </div>
                          </div>

                          <div
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: "8px"
                            }}
                          >
                            <button
                              onClick={() =>
                                quitarProducto(d.productoId)
                              }
                              style={{
                                width: "28px",
                                height: "28px",
                                border: "2px solid #ff4500",
                                background: "white",
                                color: "#ff4500",
                                fontWeight: "700",
                                borderRadius: "6px"
                              }}
                            >
                              −
                            </button>

                            <span
                              style={{
                                fontWeight: "700",
                                fontSize: "16px",
                                width: "30px",
                                textAlign: "center"
                              }}
                            >
                              {d.cantidad}
                            </span>

                            <button
                              onClick={() =>
                                agregarProducto(d.productoId)
                              }
                              style={{
                                width: "28px",
                                height: "28px",
                                border: "2px solid #4caf50",
                                background: "white",
                                color: "#4caf50",
                                fontWeight: "700",
                                borderRadius: "6px"
                              }}
                            >
                              +
                            </button>
                          </div>
                        </div>
                      );
                    })}
                  </div>

                  {/* TOTAL */}
                  <div
                    style={{
                      background:
                        "linear-gradient(135deg, #ff8c00 0%, #ff4500 100%)",
                      borderRadius: "12px",
                      padding: "20px",
                      textAlign: "center",
                      marginBottom: "20px"
                    }}
                  >
                    <div
                      style={{
                        color: "white",
                        opacity: 0.9,
                        fontSize: "14px"
                      }}
                    >
                      Total a Pagar
                    </div>
                    <div
                      style={{
                        color: "white",
                        fontSize: "36px",
                        fontWeight: "800"
                      }}
                    >
                      ${calcularTotal().toFixed(2)}
                    </div>
                  </div>

                  {/* BOTÓN REGISTRAR */}
                  <button
                    onClick={registrarPedido}
                    style={{
                      width: "100%",
                      padding: "16px",
                      border: "none",
                      borderRadius: "12px",
                      fontWeight: "700",
                      fontSize: "16px",
                      cursor: "pointer",
                      color: "white",
                      background:
                        "linear-gradient(135deg, #4caf50 0%, #2e7d32 100%)",
                      transition: "transform 0.2s ease"
                    }}
                    onMouseEnter={(e) =>
                      (e.currentTarget.style.transform = "scale(1.03)")
                    }
                    onMouseLeave={(e) =>
                      (e.currentTarget.style.transform = "scale(1)")
                    }
                  >
                    ✔ Registrar Pedido
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EntradaPedidos;
