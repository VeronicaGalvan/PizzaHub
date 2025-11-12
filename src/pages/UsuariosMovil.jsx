import React, { useState } from 'react'
import {
  CCard,
  CCardHeader,
  CCardBody,
  CListGroup,
  CListGroupItem,
  CAvatar,
  CRow,
  CCol,
  CFormInput,
  CInputGroup,
  CInputGroupText,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilSearch } from '@coreui/icons'

const UsuariosMovil = () => {
  const [busqueda, setBusqueda] = useState('')

  const usuarios = [
    {
      id: 1,
      nombre: 'Juan Pérez',
      telefono: '3312345678',
      correo: 'juanp@example.com',
      avatar: 'https://i.pravatar.cc/100?img=1',
    },
    {
      id: 2,
      nombre: 'María López',
      telefono: '3322221111',
      correo: 'mlopez@example.com',
      avatar: 'https://i.pravatar.cc/100?img=5',
    },
    {
      id: 3,
      nombre: 'Carlos Medina',
      telefono: '3339876543',
      correo: 'cmedina@example.com',
      avatar: 'https://i.pravatar.cc/100?img=12',
    },
    {
      id: 4,
      nombre: 'Fernanda Díaz',
      telefono: '3344445555',
      correo: 'fdiaz@example.com',
      avatar: 'https://i.pravatar.cc/100?img=8',
    },
  ]

  const filtrados = usuarios.filter((u) =>
    u.nombre.toLowerCase().includes(busqueda.toLowerCase())
  )

  return (
    <CCard>
      <CCardHeader
        style={{
          background: '#D35400',
          color: 'white',
          fontWeight: 'bold',
          fontSize: '1.2rem',
          letterSpacing: '1px',
        }}
      >
        Usuarios Móviles
      </CCardHeader>

      <CCardBody style={{ background: '#fafafa' }}>
        {/* BUSCADOR */}
        <CRow className="mb-4">
          <CCol md={6} lg={4}>
            <CInputGroup>
              <CInputGroupText>
                <CIcon icon={cilSearch} />
              </CInputGroupText>
              <CFormInput
                placeholder="Buscar usuario..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </CInputGroup>
          </CCol>
        </CRow>

        {/* LISTA DE USUARIOS */}
        <CListGroup>
          {filtrados.length > 0 ? (
            filtrados.map((user) => (
              <CListGroupItem
                key={user.id}
                className="d-flex align-items-center"
                style={{
                  background: 'white',
                  borderRadius: '10px',
                  marginBottom: '10px',
                  padding: '15px',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
                  cursor: 'pointer',
                  transition: '0.2s',
                }}
                onMouseEnter={(e) => (e.currentTarget.style.background = '#f0f0f0')}
                onMouseLeave={(e) => (e.currentTarget.style.background = 'white')}
              >
                {/* AVATAR */}
                <CAvatar
                  src={user.avatar}
                  size="lg"
                  className="me-3"
                  style={{ border: '2px solid #D35400' }}
                />

                {/* INFO */}
                <div>
                  <h6 style={{ margin: 0, fontWeight: '600', color: '#333' }}>
                    {user.nombre}
                  </h6>
                  <p style={{ margin: 0, color: '#666', fontSize: '0.9rem' }}>
                    {user.telefono}
                  </p>
                  <p style={{ margin: 0, color: '#999', fontSize: '0.85rem' }}>
                    {user.correo}
                  </p>
                </div>
              </CListGroupItem>
            ))
          ) : (
            <p className="text-center text-muted py-4">
              No se encontraron usuarios con "{busqueda}"
            </p>
          )}
        </CListGroup>
      </CCardBody>
    </CCard>
  )
}

export default UsuariosMovil
