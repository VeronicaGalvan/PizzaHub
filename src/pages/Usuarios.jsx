import React, { useState, useEffect } from 'react'
import {
  CCol,
  CRow,
  CButton,
  CForm,
  CFormInput,
  CFormLabel,
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
import { cilUser, cilLockLocked, cilEnvelopeClosed, cilUserPlus, cilPhone } from '@coreui/icons'

// 🔹 Convertir rol numérico a texto
const rolToText = (rol) => {
  switch (rol) {
    case 0: return "Administrador"
    case 1: return "Repartidor"
    case 2: return "Empleado"
    case 3: return "Cliente"
    default: return "Desconocido"
  }
}

const Usuarios = () => {

  const [formData, setFormData] = useState({
    nombreUsuario: '',
    email: '',
    password: '',
    telefonoContacto: ''
  })

  const [usuarios, setUsuarios] = useState([])

  // 🔹 Cargar usuarios
  const fetchUsuarios = async () => {
    try {
      const token = localStorage.getItem("token")

      const res = await fetch("https://pizzahub-api.onrender.com/api/Clientes", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      })

      if (!res.ok) {
        console.error("Error cargando usuarios:", res.status)
        return
      }

      const data = await res.json()
      setUsuarios(data)

    } catch (err) {
      console.error("Error:", err)
    }
  }

  useEffect(() => {
    fetchUsuarios()
  }, [])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      const response = await fetch('https://pizzahub-api.onrender.com/api/v1/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const err = await response.text()
        console.log("Error:", err)
        alert("Error al registrar usuario (400)")
        return
      }

      alert("Cliente registrado correctamente ✔")

      setFormData({
        nombreUsuario: "",
        email: "",
        password: "",
        telefonoContacto: ""
      })

      fetchUsuarios()

    } catch (error) {
      console.error("Error:", error)
      alert("Error en el servidor")
    }
  }


  return (
    <>

      {/* ------------------ FORMULARIO MODERNO ------------------ */}
      <CCard className="shadow-lg mb-4" style={{ borderRadius: '15px' }}>
        <CCardHeader
          style={{
            background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
            color: 'white',
            padding: '18px 20px',
            borderTopLeftRadius: '15px',
            borderTopRightRadius: '15px'
          }}
        >
          <div className="d-flex align-items-center">
            <CIcon icon={cilUser} size="lg" className="me-2" />
            <h5 className="mb-0 fw-bold">Registrar Nuevo Usuario</h5>
          </div>
        </CCardHeader>

        <CCardBody style={{ backgroundColor: '#f9fafc', padding: '25px' }}>
          <CForm onSubmit={handleSubmit}>
            <CRow className="g-4">

              <CCol md={6}>
                <CFormLabel className="fw-semibold">Nombre de Usuario</CFormLabel>
                <CInputGroup className="shadow-sm">
                  <CInputGroupText><CIcon icon={cilUser} /></CInputGroupText>
                  <CFormInput
                    name="nombreUsuario"
                    value={formData.nombreUsuario}
                    onChange={handleInputChange}
                    placeholder="usuario123"
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel className="fw-semibold">Correo Electrónico</CFormLabel>
                <CInputGroup className="shadow-sm">
                  <CInputGroupText><CIcon icon={cilEnvelopeClosed} /></CInputGroupText>
                  <CFormInput
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    placeholder="correo@ejemplo.com"
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel className="fw-semibold">Contraseña</CFormLabel>
                <CInputGroup className="shadow-sm">
                  <CInputGroupText><CIcon icon={cilLockLocked} /></CInputGroupText>
                  <CFormInput
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="•••••••"
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel className="fw-semibold">Teléfono</CFormLabel>
                <CInputGroup className="shadow-sm">
                  <CInputGroupText><CIcon icon={cilPhone} /></CInputGroupText>
                  <CFormInput
                    name="telefonoContacto"
                    value={formData.telefonoContacto}
                    onChange={handleInputChange}
                    placeholder="4771234567"
                  />
                </CInputGroup>
              </CCol>

              <CCol xs={12}>
                <CButton
                  type="submit"
                  className="w-100 shadow-sm"
                  style={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    border: 'none',
                    padding: '12px',
                    fontWeight: '600',
                    color: 'white',
                    borderRadius: '10px'
                  }}
                >
                  <CIcon icon={cilUserPlus} className="me-2" />
                  Registrar Usuario
                </CButton>
              </CCol>

            </CRow>
          </CForm>
        </CCardBody>
      </CCard>



      {/* ------------------ TABLA DE USUARIOS ------------------ */}
      <CCard className="shadow-lg" style={{ borderRadius: '15px' }}>
        <CCardHeader
          style={{
            background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
            color: 'white',
            borderTopLeftRadius: '15px',
            borderTopRightRadius: '15px',
            padding: '18px 20px'
          }}
        >
          <h5 className="mb-0 fw-bold">📋 Lista de Usuarios</h5>
        </CCardHeader>

        <CCardBody style={{ padding: '25px' }}>
          <CTable hover responsive className="shadow-sm rounded">
            <CTableHead style={{ backgroundColor: '#f1f3f5' }}>
              <CTableRow>
                <CTableHeaderCell className="fw-bold">Nombre</CTableHeaderCell>
                <CTableHeaderCell className="fw-bold">Correo</CTableHeaderCell>
                <CTableHeaderCell className="fw-bold">Teléfono</CTableHeaderCell>
                <CTableHeaderCell className="fw-bold">Rol</CTableHeaderCell>
                <CTableHeaderCell className="fw-bold">Fecha</CTableHeaderCell>
              </CTableRow>
            </CTableHead>

            <CTableBody>
              {usuarios.map((u) => {
                const usuarioObj = u.usuario || {}
                return (
                  <CTableRow key={u.id}>
                    <CTableDataCell>{usuarioObj.nombreUsuario || u.nombre}</CTableDataCell>
                    <CTableDataCell>{usuarioObj.correo || '-'}</CTableDataCell>
                    <CTableDataCell>{u.telefono || usuarioObj.telefono || '-'}</CTableDataCell>
                    <CTableDataCell>{rolToText(usuarioObj.rol)}</CTableDataCell>
                    <CTableDataCell>
                      {usuarioObj.fechaCreacion
                        ? new Date(usuarioObj.fechaCreacion).toLocaleDateString()
                        : '-'}
                    </CTableDataCell>
                  </CTableRow>
                )
              })}
            </CTableBody>

          </CTable>

          {usuarios.length === 0 && (
            <div className="text-center text-muted py-4">
              <p>No hay usuarios registrados</p>
            </div>
          )}
        </CCardBody>
      </CCard>

    </>
  )
}

export default Usuarios
