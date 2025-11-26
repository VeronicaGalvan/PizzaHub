import React, { useState, useEffect } from 'react'
import {
  CCol,
  CRow,
  CButton,
  CForm,
  CFormInput,
  CFormLabel,
  CFormSelect,
  CCard,
  CCardBody,
  CCardHeader,
  CInputGroup,
  CInputGroupText,
  CTable,
  CTableHead,
  CTableRow,
  CTableHeaderCell,
  CTableBody,
  CTableDataCell
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilUser, cilUserPlus, cilPhone, cilTruck } from '@coreui/icons'

const Repartidores = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    apellidos: '',
    telefono: '',
    usuarioId: ''
  })

  const [usuarios, setUsuarios] = useState([])
  const [repartidores, setRepartidores] = useState([])

  // 🔹 Cargar usuarios con rol 2
  const fetchUsuarios = async () => {
    try {
      const token = localStorage.getItem('token')
      const res = await fetch('https://pizzahub-api.onrender.com/api/Clientes', {
        headers: { Authorization: `Bearer ${token}` },
      })

      if (!res.ok) return

      const data = await res.json()

      const empleados = data
        .filter(u => u.usuario && u.usuario.rol === 2)
        .map(u => ({
          id: u.usuario.id,
          nombreUsuario: u.usuario.nombreUsuario
        }))

      setUsuarios(empleados)
    } catch (err) {
      console.error("Error cargando usuarios:", err)
    }
  }

  // 🔹 Cargar repartidores
  const fetchRepartidores = async () => {
    try {
      const token = localStorage.getItem('token')
      const res = await fetch('https://pizzahub-api.onrender.com/api/Repartidores', {
        headers: { Authorization: `Bearer ${token}` },
      })

      if (!res.ok) return

      const data = await res.json()
      setRepartidores(data)
    } catch (err) {
      console.error("Error cargando repartidores:", err)
    }
  }

  useEffect(() => {
    fetchUsuarios()
    fetchRepartidores()
  }, [])

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      const token = localStorage.getItem('token')

      const res = await fetch('https://pizzahub-api.onrender.com/api/Repartidores', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          nombre: formData.nombre,
          apellidos: formData.apellidos,
          telefono: formData.telefono,
          usuarioId: parseInt(formData.usuarioId)
        })
      })

      if (!res.ok) {
        alert("Error al registrar el repartidor")
        return
      }

      alert("Repartidor registrado correctamente ✔")

      // Reset form
      setFormData({
        nombre: '',
        apellidos: '',
        telefono: '',
        usuarioId: ''
      })

      // Refrescar tabla
      fetchRepartidores()
    } catch (err) {
      console.error("Error al registrar repartidor:", err)
    }
  }

  return (
    <>
      {/* FORMULARIO */}
      <CCard className="shadow-sm mb-4">
        <CCardHeader style={{ background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', color: 'white' }}>
          <div className="d-flex align-items-center">
            <CIcon icon={cilTruck} size="lg" className="me-2" />
            <h5 className="mb-0">Registrar Repartidor</h5>
          </div>
        </CCardHeader>

        <CCardBody style={{ backgroundColor: '#f8f9fa' }}>
          <CForm onSubmit={handleSubmit}>
            <CRow className="g-3">

              {/* Usuario */}
              <CCol md={6}>
                <CFormLabel>Usuario</CFormLabel>
                <CFormSelect
                  name="usuarioId"
                  value={formData.usuarioId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Seleccione un usuario --</option>
                  {usuarios.map(u => (
                    <option key={u.id} value={u.id}>
                      {u.nombreUsuario}
                    </option>
                  ))}
                </CFormSelect>
              </CCol>

              {/* Nombre */}
              <CCol md={6}>
                <CFormLabel>Nombre</CFormLabel>
                <CInputGroup>
                  <CInputGroupText><CIcon icon={cilUser} /></CInputGroupText>
                  <CFormInput
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleInputChange}
                    placeholder="Nombre del repartidor"
                  />
                </CInputGroup>
              </CCol>

              {/* Apellidos */}
              <CCol md={6}>
                <CFormLabel>Apellidos</CFormLabel>
                <CInputGroup>
                  <CInputGroupText><CIcon icon={cilUser} /></CInputGroupText>
                  <CFormInput
                    name="apellidos"
                    value={formData.apellidos}
                    onChange={handleInputChange}
                    placeholder="Apellidos"
                  />
                </CInputGroup>
              </CCol>

              {/* Teléfono */}
              <CCol md={6}>
                <CFormLabel>Teléfono</CFormLabel>
                <CInputGroup>
                  <CInputGroupText><CIcon icon={cilPhone} /></CInputGroupText>
                  <CFormInput
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleInputChange}
                    placeholder="4771234567"
                  />
                </CInputGroup>
              </CCol>

              {/* Botón */}
              <CCol xs={12}>
                <CButton
                  type="submit"
                  style={{
                    width: '100%',
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    border: 'none',
                    padding: '12px',
                    fontWeight: '600'
                  }}
                >
                  <CIcon icon={cilUserPlus} className="me-2" />
                  Registrar Repartidor
                </CButton>
              </CCol>

            </CRow>
          </CForm>
        </CCardBody>
      </CCard>

      {/* TABLA DE REPARTIDORES */}
      <CCard className="shadow-sm">
        <CCardHeader style={{ background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', color: 'white' }}>
          <h5 className="mb-0">Lista de Repartidores</h5>
        </CCardHeader>

        <CCardBody>
          <CTable hover bordered responsive>
            <CTableHead>
              <CTableRow>
                <CTableHeaderCell>ID</CTableHeaderCell>
                <CTableHeaderCell>Nombre</CTableHeaderCell>
                <CTableHeaderCell>Teléfono</CTableHeaderCell>
                <CTableHeaderCell>Usuario</CTableHeaderCell>
                <CTableHeaderCell>Estado</CTableHeaderCell>
              </CTableRow>
            </CTableHead>

            <CTableBody>
              {repartidores.map(r => (
                <CTableRow key={r.id}>
                  <CTableDataCell>{r.id}</CTableDataCell>
                  <CTableDataCell>{r.nombre} {r.apellidos}</CTableDataCell>
                  <CTableDataCell>{r.telefono}</CTableDataCell>
                  <CTableDataCell>{r.usuario?.nombreUsuario}</CTableDataCell>
                  <CTableDataCell>
                    {r.estado === 0 ? "Activo" : "Inactivo"}
                  </CTableDataCell>
                </CTableRow>
              ))}
            </CTableBody>
          </CTable>
        </CCardBody>
      </CCard>
    </>
  )
}

export default Repartidores
