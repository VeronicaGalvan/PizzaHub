import React, { useEffect, useState, useContext } from "react";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CButton,
  CFormInput,
  CAlert,
  CSpinner,
  CTable,
  CTableHead,
  CTableRow,
  CTableHeaderCell,
  CTableBody,
  CTableDataCell,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
} from "@coreui/react";
import AuthContext from "../context/AuthContext";
import {
  getCajaAbierta,
  abrirCaja as abrirCajaAPI,
  cerrarCaja as cerrarCajaAPI,
} from "../api/caja";
import { getMiEmpleado } from "../api/empleados";

const Caja = () => {
  const { token, user, roles } = useContext(AuthContext);
  
  const [cajaAbierta, setCajaAbierta] = useState(null);
  const [empleadoInfo, setEmpleadoInfo] = useState(null);
  const [saldoInicial, setSaldoInicial] = useState("");
  const [saldoFinal, setSaldoFinal] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [resumen, setResumen] = useState(null);
  const [showResumenModal, setShowResumenModal] = useState(false);

  // Obtener empleadoId del usuario logueado o del empleadoInfo cargado
  const empleadoId = user?.empleadoId || empleadoInfo?.id || null;

  // -------------------------------
  // Obtener información del empleado
  // -------------------------------
  const fetchEmpleadoInfo = async () => {
    // Solo intentar obtener info si el usuario es Empleado o Administrador
    if (!roles?.includes("Empleado") && !roles?.includes("Administrador")) {
      return;
    }

    try {
      const empleado = await getMiEmpleado(token, user?.id);
      setEmpleadoInfo(empleado);
    } catch (err) {
      console.error("Error obteniendo información del empleado:", err);
      // No mostrar error al usuario, solo loguear
    }
  };

  // -------------------------------
  // Consultar caja abierta
  // -------------------------------
  const fetchCaja = async () => {
    try {
      setError(null);
      const data = await getCajaAbierta(token);
      setCajaAbierta(data);
    } catch (err) {
      console.error("Error consultando caja:", err);
      if (err.message !== "Error al obtener la caja abierta") {
        setError(err.message);
      }
    }
  };

  useEffect(() => {
    fetchCaja();
    fetchEmpleadoInfo();
  }, []);

  // -------------------------------
  // Abrir caja
  // -------------------------------
  const handleAbrirCaja = async () => {
    if (!saldoInicial || parseFloat(saldoInicial) < 0) {
      setError("Ingrese un saldo inicial válido");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      await abrirCajaAPI(token, saldoInicial, empleadoId);
      setSuccess("✅ Caja abierta exitosamente");
      setSaldoInicial("");
      await fetchCaja();
    } catch (err) {
      console.error(err);
      setError(err.message || "Error al abrir la caja");
    } finally {
      setLoading(false);
    }
  };

  // -------------------------------
  // Cerrar caja
  // -------------------------------
  const handleCerrarCaja = async () => {
    if (!cajaAbierta) return;
    
    if (!saldoFinal || parseFloat(saldoFinal) < 0) {
      setError("Ingrese un saldo final válido");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const resumenData = await cerrarCajaAPI(token, cajaAbierta.id, saldoFinal);
      setResumen(resumenData);
      setSuccess("✅ Caja cerrada correctamente");
      setSaldoFinal("");
      setCajaAbierta(null);
      setShowResumenModal(true);
    } catch (err) {
      console.error(err);
      setError(err.message || "Error al cerrar la caja");
    } finally {
      setLoading(false);
    }
  };

  // -------------------------------
  // Cerrar modal de resumen
  // -------------------------------
  const handleCloseResumen = () => {
    setShowResumenModal(false);
    setResumen(null);
  };

  return (
    <div className="container mt-4" style={{ maxWidth: "800px" }}>
      <h2 className="text-center mb-4 fw-bold">Gestión de Caja</h2>

      {/* Mensajes de error y éxito */}
      {error && (
        <CAlert color="danger" dismissible onClose={() => setError(null)}>
          {error}
        </CAlert>
      )}

      {success && (
        <CAlert color="success" dismissible onClose={() => setSuccess(null)}>
          {success}
        </CAlert>
      )}

      {/* Si NO hay caja abierta → mostrar abrir */}
      {!cajaAbierta && (
        <CCard className="shadow-sm mb-4">
          <CCardHeader
            style={{
              background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
              color: "white",
            }}
          >
            <h5 className="mb-0">🏦 Abrir Caja</h5>
          </CCardHeader>

          <CCardBody style={{ backgroundColor: "#f8f9fa" }}>
            {(empleadoInfo || user?.nombre) && (
              <div className="alert alert-info mb-3">
                <small>
                  <strong>👤 Empleado:</strong>{" "}
                  {empleadoInfo
                    ? `${empleadoInfo.nombre} ${empleadoInfo.apellidos}`
                    : user?.nombreUsuario || "No especificado"}
                </small>
              </div>
            )}

            <label className="form-label mt-2">
              Saldo Inicial <span className="text-danger">*</span>
            </label>
            <CFormInput
              type="number"
              step="0.01"
              min="0"
              value={saldoInicial}
              onChange={(e) => setSaldoInicial(e.target.value)}
              placeholder="Ej: 500.00"
              className="mb-3"
              disabled={loading}
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
              onClick={handleAbrirCaja}
              disabled={loading}
            >
              {loading ? (
                <>
                  <CSpinner size="sm" className="me-2" /> Procesando...
                </>
              ) : (
                "🔓 Abrir Caja"
              )}
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
            <h5 className="mb-0">🔒 Cerrar Caja</h5>
          </CCardHeader>

          <CCardBody style={{ backgroundColor: "#f8f9fa" }}>
            <div className="alert alert-warning mb-3">
              <p className="mb-1">
                <strong>📅 Fecha:</strong>{" "}
                {new Date(cajaAbierta.fecha).toLocaleDateString("es-MX")}
              </p>
              <p className="mb-1">
                <strong>💵 Saldo inicial:</strong> $
                {cajaAbierta.saldoInicial.toFixed(2)}
              </p>
              {cajaAbierta.empleado && (
                <p className="mb-0">
                  <strong>👤 Empleado:</strong>{" "}
                  {cajaAbierta.empleado.nombre} {cajaAbierta.empleado.apellidos}
                </p>
              )}
            </div>

            <label className="form-label mt-2">
              Saldo Final <span className="text-danger">*</span>
            </label>
            <CFormInput
              type="number"
              step="0.01"
              min="0"
              value={saldoFinal}
              onChange={(e) => setSaldoFinal(e.target.value)}
              placeholder="Ej: 800.00"
              className="mb-3"
              disabled={loading}
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
              onClick={handleCerrarCaja}
              disabled={loading}
            >
              {loading ? (
                <>
                  <CSpinner size="sm" className="me-2" /> Procesando...
                </>
              ) : (
                "🔒 Cerrar Caja"
              )}
            </CButton>
          </CCardBody>
        </CCard>
      )}

      {/* Modal de Resumen de Caja */}
      <CModal
        visible={showResumenModal}
        onClose={handleCloseResumen}
        size="lg"
        backdrop="static"
      >
        <CModalHeader>
          <CModalTitle>📊 Resumen de Caja</CModalTitle>
        </CModalHeader>
        <CModalBody>
          {resumen && (
            <div>
              <div className="mb-4">
                <h5 className="border-bottom pb-2">Información General</h5>
                <div className="row">
                  <div className="col-md-6">
                    <p>
                      <strong>Fecha:</strong>{" "}
                      {new Date(resumen.fecha).toLocaleDateString("es-MX")}
                    </p>
                    <p>
                      <strong>Empleado:</strong>{" "}
                      {resumen.empleadoNombre || "No especificado"}
                    </p>
                  </div>
                  <div className="col-md-6">
                    <p>
                      <strong>Total de Ventas:</strong> {resumen.cantidadVentas}
                    </p>
                  </div>
                </div>
              </div>

              <div className="mb-4">
                <h5 className="border-bottom pb-2">Resumen Financiero</h5>
                <CTable striped bordered hover responsive>
                  <CTableBody>
                    <CTableRow>
                      <CTableDataCell>
                        <strong>Saldo Inicial</strong>
                      </CTableDataCell>
                      <CTableDataCell className="text-end">
                        ${resumen.saldoInicial.toFixed(2)}
                      </CTableDataCell>
                    </CTableRow>
                    <CTableRow>
                      <CTableDataCell>
                        <strong>Total Ventas</strong>
                      </CTableDataCell>
                      <CTableDataCell className="text-end text-success">
                        + ${resumen.totalVentas.toFixed(2)}
                      </CTableDataCell>
                    </CTableRow>
                    <CTableRow>
                      <CTableDataCell>
                        <strong>Esperado</strong>
                      </CTableDataCell>
                      <CTableDataCell className="text-end">
                        ${(resumen.saldoInicial + resumen.totalVentas).toFixed(2)}
                      </CTableDataCell>
                    </CTableRow>
                    <CTableRow className="table-primary">
                      <CTableDataCell>
                        <strong>Saldo Final (Real)</strong>
                      </CTableDataCell>
                      <CTableDataCell className="text-end">
                        <strong>${resumen.saldoFinal.toFixed(2)}</strong>
                      </CTableDataCell>
                    </CTableRow>
                    <CTableRow
                      color={
                        resumen.saldoFinal ===
                        resumen.saldoInicial + resumen.totalVentas
                          ? "success"
                          : "warning"
                      }
                    >
                      <CTableDataCell>
                        <strong>Diferencia</strong>
                      </CTableDataCell>
                      <CTableDataCell className="text-end">
                        <strong>
                          $
                          {(
                            resumen.saldoFinal -
                            (resumen.saldoInicial + resumen.totalVentas)
                          ).toFixed(2)}
                        </strong>
                      </CTableDataCell>
                    </CTableRow>
                  </CTableBody>
                </CTable>
              </div>

              {resumen.ventasPorMetodoPago &&
                Object.keys(resumen.ventasPorMetodoPago).length > 0 && (
                  <div className="mb-3">
                    <h5 className="border-bottom pb-2">
                      Ventas por Método de Pago
                    </h5>
                    <CTable striped bordered hover responsive>
                      <CTableHead>
                        <CTableRow>
                          <CTableHeaderCell>Método de Pago</CTableHeaderCell>
                          <CTableHeaderCell className="text-end">
                            Total
                          </CTableHeaderCell>
                        </CTableRow>
                      </CTableHead>
                      <CTableBody>
                        {Object.entries(resumen.ventasPorMetodoPago).map(
                          ([metodo, total]) => (
                            <CTableRow key={metodo}>
                              <CTableDataCell>
                                {metodo === "Efectivo" && "💵 Efectivo"}
                                {metodo === "Tarjeta" && "💳 Tarjeta"}
                                {metodo === "Transferencia" && "🏦 Transferencia"}
                                {!["Efectivo", "Tarjeta", "Transferencia"].includes(
                                  metodo
                                ) && metodo}
                              </CTableDataCell>
                              <CTableDataCell className="text-end">
                                ${total.toFixed(2)}
                              </CTableDataCell>
                            </CTableRow>
                          )
                        )}
                      </CTableBody>
                    </CTable>
                  </div>
                )}
            </div>
          )}
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={handleCloseResumen}>
            Cerrar
          </CButton>
        </CModalFooter>
      </CModal>
    </div>
  );
};

export default Caja;
