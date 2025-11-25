import React, { useEffect, useState } from "react"
import {
  CCard,
  CCardHeader,
  CCardBody,
  CForm,
  CRow,
  CCol,
  CFormLabel,
  CFormInput,
  CFormTextarea,
  CFormSelect,
  CButton
} from "@coreui/react"

const CompraInsumos = () => {
  const [insumos, setInsumos] = useState([])
  const [loadingInsumos, setLoadingInsumos] = useState(true)

  const initialFormState = {
    proveedor: "",
    numeroFactura: "",
    observaciones: "",
    detalles: [
      { insumoId: "", cantidad: "", precioUnitario: "" }
    ]
  }

  const [formData, setFormData] = useState(initialFormState)

  useEffect(() => {
    const fetchInsumos = async () => {
      try {
        const token = localStorage.getItem("token")
        const res = await fetch("https://localhost:7188/api/Insumos", {
          headers: { Authorization: `Bearer ${token}` }
        })

        if (!res.ok) throw new Error("Error al obtener insumos")

        const data = await res.json()
        setInsumos(data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoadingInsumos(false)
      }
    }

    fetchInsumos()
  }, [])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData({ ...formData, [name]: value })
  }

  const handleDetalleChange = (index, field, value) => {
    const nuevosDetalles = [...formData.detalles]
    nuevosDetalles[index][field] = value
    setFormData({ ...formData, detalles: nuevosDetalles })
  }

  const agregarDetalle = () => {
    setFormData({
      ...formData,
      detalles: [
        ...formData.detalles,
        { insumoId: "", cantidad: "", precioUnitario: "" }
      ]
    })
  }

  const eliminarDetalle = (index) => {
    const nuevosDetalles = formData.detalles.filter((_, i) => i !== index)
    setFormData({ ...formData, detalles: nuevosDetalles })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      const token = localStorage.getItem("token")

      const res = await fetch("https://localhost:7188/api/ComprasInsumos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      })

      if (!res.ok) throw new Error("Error al registrar compra")

      alert("Compra registrada correctamente")
      setFormData(initialFormState)

    } catch (err) {
      console.error(err)
      alert("Error al registrar compra")
    }
  }

  // ============================
  // Estilos globales del diseño
  // ============================
  const inputStyle = {
    padding: "12px",
    borderRadius: "10px",
    border: "2px solid #d7dce5",
    transition: "0.25s",
  }

  const inputFocus = (e) => (e.target.style.borderColor = "#2673ff")
  const inputBlur = (e) => (e.target.style.borderColor = "#d7dce5")

  return (
    <div
      style={{
        padding: "25px",
        background: "#f4f6f9",
        borderRadius: "20px"
      }}
    >
      <CCard
        className="shadow-lg"
        style={{
          borderRadius: "20px",
          overflow: "hidden",
          border: "1px solid #e2e6ee"
        }}
      >
        <CCardHeader
          style={{
            background: "linear-gradient(135deg, #0055ff, #00308f)",
            color: "white",
            padding: "25px"
          }}
        >
          <h3 className="mb-0" style={{ fontWeight: "700" }}>
            🛒 Registrar Compra de Insumos
          </h3>
        </CCardHeader>

        <CCardBody style={{ backgroundColor: "#ffffff", padding: "35px" }}>
          <CForm onSubmit={handleSubmit}>
            <CRow className="g-4">

              {/* Proveedor */}
              <CCol md={6}>
                <CFormLabel>Proveedor</CFormLabel>
                <CFormInput
                  name="proveedor"
                  placeholder="Proveedor..."
                  style={inputStyle}
                  onFocus={inputFocus}
                  onBlur={inputBlur}
                  value={formData.proveedor}
                  onChange={handleInputChange}
                />
              </CCol>

              {/* Factura */}
              <CCol md={6}>
                <CFormLabel>Número de Factura</CFormLabel>
                <CFormInput
                  name="numeroFactura"
                  placeholder="F-0001"
                  style={inputStyle}
                  onFocus={inputFocus}
                  onBlur={inputBlur}
                  value={formData.numeroFactura}
                  onChange={handleInputChange}
                />
              </CCol>

              {/* Observaciones */}
              <CCol xs={12}>
                <CFormLabel>Observaciones</CFormLabel>
                <CFormTextarea
                  rows={3}
                  name="observaciones"
                  placeholder="Notas adicionales..."
                  style={{ ...inputStyle, resize: "none" }}
                  onFocus={inputFocus}
                  onBlur={inputBlur}
                  value={formData.observaciones}
                  onChange={handleInputChange}
                />
              </CCol>

              {/* Sección Detalles */}
              <CCol xs={12}>
                <hr />
                <h5 style={{ fontWeight: "700", color: "#1a1a1a" }}>
                  📦 Detalles de la Compra
                </h5>
              </CCol>

              {formData.detalles.map((detalle, index) => (
                <CRow key={index} className="g-3 mb-3 align-items-end">
                  
                  {/* Insumo */}
                  <CCol md={4}>
                    <CFormLabel>Insumo</CFormLabel>
                    <CFormSelect
                      value={detalle.insumoId}
                      style={inputStyle}
                      onFocus={inputFocus}
                      onBlur={inputBlur}
                      onChange={(e) =>
                        handleDetalleChange(index, "insumoId", e.target.value)
                      }
                    >
                      <option value="">Seleccione insumo...</option>
                      {!loadingInsumos &&
                        insumos.map((i) => (
                          <option key={i.id} value={i.id}>
                            {i.nombre}
                          </option>
                        ))}
                    </CFormSelect>
                  </CCol>

                  {/* Cantidad */}
                  <CCol md={3}>
                    <CFormLabel>Cantidad</CFormLabel>
                    <CFormInput
                      type="number"
                      style={inputStyle}
                      onFocus={inputFocus}
                      onBlur={inputBlur}
                      value={detalle.cantidad}
                      onChange={(e) =>
                        handleDetalleChange(index, "cantidad", e.target.value)
                      }
                    />
                  </CCol>

                  {/* Precio */}
                  <CCol md={3}>
                    <CFormLabel>Precio Unitario</CFormLabel>
                    <CFormInput
                      type="number"
                      step="0.01"
                      style={inputStyle}
                      onFocus={inputFocus}
                      onBlur={inputBlur}
                      value={detalle.precioUnitario}
                      onChange={(e) =>
                        handleDetalleChange(index, "precioUnitario", e.target.value)
                      }
                    />
                  </CCol>

                  {/* Eliminar */}
                  <CCol md={2}>
                    <CButton
                      color="danger"
                      className="w-100"
                      onClick={() => eliminarDetalle(index)}
                    >
                      ❌ Eliminar
                    </CButton>
                  </CCol>
                </CRow>
              ))}

              {/* Agregar detalle */}
              <CCol xs={12}>
                <CButton color="info" onClick={agregarDetalle}>
                  ➕ Agregar Insumo
                </CButton>
              </CCol>

              {/* Guardar */}
              <CCol xs={12} className="text-end mt-4">
                <CButton
                  type="submit"
                  color="primary"
                  style={{ padding: "12px 22px", fontWeight: "700" }}
                >
                  Registrar Compra
                </CButton>
              </CCol>

            </CRow>
          </CForm>
        </CCardBody>
      </CCard>
    </div>
  )
}

export default CompraInsumos
