import React, { useEffect, useState } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CFormInput,
} from "@coreui/react";

const Caja = () => {
  const [cajaAbierta, setCajaAbierta] = useState(null);
  const [saldoInicial, setSaldoInicial] = useState("");
  const [saldoFinal, setSaldoFinal] = useState("");
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("token");

  // -------------------------------
  // Consultar caja abierta
  // -------------------------------
  const fetchCaja = async () => {
    try {
      const res = await fetch("https://localhost:7188/api/Caja/abierta", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.status === 401) {
        console.log("Token inválido o expirado");
        return;
      }

      if (!res.ok) {
        setCajaAbierta(null);
        return;
      }

      const data = await res.json();
      setCajaAbierta(data);
    } catch (error) {
      console.error("Error consultando caja:", error);
    }
  };

  useEffect(() => {
    fetchCaja();
  }, []);

  // -------------------------------
  // Abrir caja
  // -------------------------------
  const abrirCaja = async () => {
    if (saldoInicial === "") return alert("Ingrese saldo inicial");

    setLoading(true);
    try {
      const resp = await fetch("https://localhost:7188/api/Caja/abrir", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          saldoInicial: parseFloat(saldoInicial),
          empleadoId: 1,
        }),
      });

      if (resp.status === 401) {
        alert("No autorizado. Inicie sesión nuevamente.");
        return;
      }

      if (!resp.ok) {
        const error = await resp.json();
        alert(error.message || "Error al abrir caja");
        return;
      }

      alert("Caja abierta exitosamente");
      setSaldoInicial("");
      fetchCaja();
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }

    setLoading(false);
  };

  // -------------------------------
  // Cerrar caja
  // -------------------------------
  const cerrarCaja = async () => {
    if (!cajaAbierta) return;
    if (saldoFinal === "") return alert("Ingrese saldo final");

    setLoading(true);
    try {
      const resp = await fetch(
        `https://localhost:7188/api/Caja/${cajaAbierta.id}/cerrar`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            saldoFinal: parseFloat(saldoFinal),
          }),
        }
      );

      if (resp.status === 401) {
        alert("No autorizado. Inicie sesión nuevamente.");
        return;
      }

      if (!resp.ok) {
        const error = await resp.json();
        alert(error.message || "Error al cerrar caja");
        return;
      }

      alert("Caja cerrada correctamente");
      setSaldoFinal("");
      setCajaAbierta(null);
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }

    setLoading(false);
  };

  return (
    <div className="container mt-4" style={{ maxWidth: "600px" }}>
      <h2 className="text-center mb-4 fw-bold">Gestión de Caja</h2>

      {/* Si NO hay caja abierta → mostrar abrir */}
      {!cajaAbierta && (
        <CCard className="shadow-sm mb-4">
          <CCardHeader
            style={{
              background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
              color: "white",
            }}
          >
            <h5 className="mb-0">Abrir Caja</h5>
          </CCardHeader>

          <CCardBody style={{ backgroundColor: "#f8f9fa" }}>
            <label className="form-label mt-2">Saldo Inicial</label>
            <CFormInput
              type="number"
              value={saldoInicial}
              onChange={(e) => setSaldoInicial(e.target.value)}
              placeholder="Ej: 500"
              className="mb-3"
            />

            <CButton
              color="primary"
              className="w-100"
              style={{
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                border: "none",
                padding: "12px",
                fontWeight: "600",
              }}
              onClick={abrirCaja}
              disabled={loading}
            >
              {loading ? "Procesando..." : "Abrir Caja"}
            </CButton>
          </CCardBody>
        </CCard>
      )}

      {/* Si hay caja abierta → mostrar cerrar */}
      {cajaAbierta && (
        <CCard className="shadow-sm mb-4">
          <CCardHeader
            style={{
              background: "linear-gradient(135deg, #ff7170 0%, #ff9a8b 100%)",
              color: "white",
            }}
          >
            <h5 className="mb-0">Cerrar Caja</h5>
          </CCardHeader>

          <CCardBody style={{ backgroundColor: "#f8f9fa" }}>
            <p>
              <strong>Saldo inicial:</strong> ${cajaAbierta.saldoInicial}
            </p>

            <label className="form-label mt-2">Saldo Final</label>
            <CFormInput
              type="number"
              value={saldoFinal}
              onChange={(e) => setSaldoFinal(e.target.value)}
              placeholder="Ej: 800"
              className="mb-3"
            />

            <CButton
              color="danger"
              className="w-100"
              style={{
                background: "linear-gradient(135deg, #ff5858 0%, #f857a6 100%)",
                border: "none",
                padding: "12px",
                fontWeight: "600",
              }}
              onClick={cerrarCaja}
              disabled={loading}
            >
              {loading ? "Procesando..." : "Cerrar Caja"}
            </CButton>
          </CCardBody>
        </CCard>
      )}
    </div>
  );
};

export default Caja;
