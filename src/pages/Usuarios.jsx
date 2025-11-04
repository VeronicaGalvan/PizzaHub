import React, { useState } from 'react'
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
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilUser, cilLockLocked, cilEnvelopeClosed, cilUserPlus } from '@coreui/icons'

const Usuarios = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    contrasena: '',
    confirmarContrasena: '',
    tipoUsuario: 'empleado',
    telefono: ''
  })

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    console.log('Nuevo usuario:', formData)
    // Aquí iría la lógica para agregar el usuario
    alert('Usuario agregado correctamente')
  }

  return (
    <>
      <CCard className="shadow-sm mb-4">
        <CCardHeader style={{ 
          background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
          color: 'white',
          borderBottom: 'none',
          padding: '12px 20px'
        }}>
          <div className="d-flex align-items-center">
            <CIcon icon={cilUser} size="lg" className="me-2" />
            <h5 className="mb-0">Agregar Nuevo Usuario</h5>
          </div>
        </CCardHeader>
        <CCardBody style={{ backgroundColor: '#f8f9fa', padding: '20px' }}>
          <CForm onSubmit={handleSubmit}>
            <CRow className="g-3">
              {/* Información Personal */}
              <CCol md={6}>
                <CFormLabel htmlFor="nombre" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Nombre
                </CFormLabel>
                <CInputGroup>
                  <CInputGroupText style={{ backgroundColor: 'white', borderRight: 'none' }}>
                    <CIcon icon={cilUser} size="sm" />
                  </CInputGroupText>
                  <CFormInput
                    type="text"
                    id="nombre"
                    name="nombre"
                    placeholder="Nombre del usuario"
                    value={formData.nombre}
                    onChange={handleInputChange}
                    style={{ borderLeft: 'none' }}
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel htmlFor="apellido" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Apellido
                </CFormLabel>
                <CInputGroup>
                  <CInputGroupText style={{ backgroundColor: 'white', borderRight: 'none' }}>
                    <CIcon icon={cilUser} size="sm" />
                  </CInputGroupText>
                  <CFormInput
                    type="text"
                    id="apellido"
                    name="apellido"
                    placeholder="Apellido del usuario"
                    value={formData.apellido}
                    onChange={handleInputChange}
                    style={{ borderLeft: 'none' }}
                  />
                </CInputGroup>
              </CCol>

              {/* Email y Teléfono */}
              <CCol md={6}>
                <CFormLabel htmlFor="email" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Correo Electrónico
                </CFormLabel>
                <CInputGroup>
                  <CInputGroupText style={{ backgroundColor: 'white', borderRight: 'none' }}>
                    <CIcon icon={cilEnvelopeClosed} size="sm" />
                  </CInputGroupText>
                  <CFormInput
                    type="email"
                    id="email"
                    name="email"
                    placeholder="correo@ejemplo.com"
                    value={formData.email}
                    onChange={handleInputChange}
                    style={{ borderLeft: 'none' }}
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel htmlFor="telefono" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Teléfono
                </CFormLabel>
                <CFormInput
                  type="tel"
                  id="telefono"
                  name="telefono"
                  placeholder="(123) 456-7890"
                  value={formData.telefono}
                  onChange={handleInputChange}
                />
              </CCol>

              {/* Contraseñas */}
              <CCol md={6}>
                <CFormLabel htmlFor="contrasena" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Contraseña
                </CFormLabel>
                <CInputGroup>
                  <CInputGroupText style={{ backgroundColor: 'white', borderRight: 'none' }}>
                    <CIcon icon={cilLockLocked} size="sm" />
                  </CInputGroupText>
                  <CFormInput
                    type="password"
                    id="contrasena"
                    name="contrasena"
                    placeholder="••••••••"
                    value={formData.contrasena}
                    onChange={handleInputChange}
                    style={{ borderLeft: 'none' }}
                  />
                </CInputGroup>
              </CCol>

              <CCol md={6}>
                <CFormLabel htmlFor="confirmarContrasena" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Confirmar Contraseña
                </CFormLabel>
                <CInputGroup>
                  <CInputGroupText style={{ backgroundColor: 'white', borderRight: 'none' }}>
                    <CIcon icon={cilLockLocked} size="sm" />
                  </CInputGroupText>
                  <CFormInput
                    type="password"
                    id="confirmarContrasena"
                    name="confirmarContrasena"
                    placeholder="••••••••"
                    value={formData.confirmarContrasena}
                    onChange={handleInputChange}
                    style={{ borderLeft: 'none' }}
                  />
                </CInputGroup>
              </CCol>

              {/* Tipo de Usuario */}
              <CCol md={6}>
                <CFormLabel htmlFor="tipoUsuario" style={{ fontWeight: '500', color: '#495057', fontSize: '0.9rem' }}>
                  Tipo de Usuario
                </CFormLabel>
                <CFormSelect
                  id="tipoUsuario"
                  name="tipoUsuario"
                  value={formData.tipoUsuario}
                  onChange={handleInputChange}
                  style={{
                    fontWeight: '500',
                    color: formData.tipoUsuario === 'administrador' ? '#e55353' : '#2eb85c'
                  }}
                >
                  <option value="empleado">👤 Empleado</option>
                  <option value="administrador">⭐ Administrador</option>
                </CFormSelect>
              </CCol>

              {/* Botón de Agregar */}
              <CCol md={6} className="d-flex align-items-end">
                <CButton 
                  type="submit"
                  style={{
                    width: '100%',
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    border: 'none',
                    padding: '10px 20px',
                    fontWeight: '600',
                    boxShadow: '0 4px 6px rgba(102, 126, 234, 0.3)',
                    transition: 'all 0.3s',
                    fontSize: '0.95rem'
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.transform = 'translateY(-2px)'
                    e.target.style.boxShadow = '0 6px 10px rgba(102, 126, 234, 0.4)'
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.transform = 'translateY(0)'
                    e.target.style.boxShadow = '0 4px 6px rgba(102, 126, 234, 0.3)'
                  }}
                >
                  <CIcon icon={cilUserPlus} className="me-2" />
                  Agregar Usuario
                </CButton>
              </CCol>
            </CRow>
          </CForm>
        </CCardBody>
      </CCard>

      {/* Espacio reservado para la tabla de usuarios */}
      <CCard className="shadow-sm">
        <CCardHeader style={{ 
          backgroundColor: '#fff',
          borderBottom: '2px solid #e9ecef',
          padding: '15px 20px'
        }}>
          <h5 className="mb-0" style={{ color: '#495057', fontWeight: '600' }}>
            📋 Lista de Usuarios
          </h5>
        </CCardHeader>
        <CCardBody>
          <div className="text-center py-5 text-muted">
            <p style={{ fontSize: '1.1rem' }}>La tabla de usuarios aparecerá aquí</p>
            <small>Agrega usuarios utilizando el formulario de arriba</small>
          </div>
        </CCardBody>
      </CCard>
    </>
  )
}

export default Usuarios