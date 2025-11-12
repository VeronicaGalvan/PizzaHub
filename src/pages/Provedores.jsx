import React, { useState } from 'react'
import {
  CRow,
  CCol,
  CForm,
  CFormInput,
  CFormLabel,
  CButton,
  CCard,
  CCardBody,
  CCardHeader,
  CTable,
  CTableHead,
  CTableRow,
  CTableHeaderCell,
  CTableBody,
  CTableDataCell,
  CModal,
  CModalBody,
  CModalHeader,
  CInputGroup,
  CInputGroupText,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilUser, cilEnvelopeClosed, cilPhone, cilSave, cilTrash, cilPen } from '@coreui/icons'

const Provedores = () => {
  // Lista de proveedores inicial
  const [provedores, setProvedores] = useState([
    { id: 1, nombre: 'Distribuidora México', telefono: '3312345678', correo: 'ventas@dmx.com' },
    { id: 2, nombre: 'Harinas del Norte', telefono: '3323456789', correo: 'contacto@harinasnorte.com' },
  ])

  // Formulario nuevo proveedor
  const [form, setForm] = useState({
    nombre: '',
    telefono: '',
    correo: '',
  })

  // Estados para editar
  const [editModal, setEditModal] = useState(false)
  const [proveedorEditando, setProveedorEditando] = useState(null)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  // AGREGAR
  const handleAgregar = (e) => {
    e.preventDefault()

    if (!form.nombre || !form.telefono || !form.correo) return

    const nuevoProveedor = {
      id: Date.now(),
      ...form,
    }

    setProvedores([...provedores, nuevoProveedor])
    setForm({ nombre: '', telefono: '', correo: '' })
  }

  // ELIMINAR
  const handleEliminar = (id) => {
    setProvedores(provedores.filter((p) => p.id !== id))
  }

  // ABRIR MODAL EDITAR
  const abrirEditar = (proveedor) => {
    setProveedorEditando(proveedor)
    setEditModal(true)
  }

  // GUARDAR EDICIÓN
  const handleGuardarEdicion = () => {
    setProvedores(
      provedores.map((p) =>
        p.id === proveedorEditando.id ? proveedorEditando : p
      )
    )
    setEditModal(false)
  }

  return (
    <>
      {/* ENCABEZADO */}
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
          Gestión de Proveedores
        </CCardHeader>

        <CCardBody style={{ background: '#fafafa' }}>
          <CRow>
            {/* FORM AGREGAR */}
            <CCol md={4}>
              <h5 style={{ fontWeight: 'bold', color: '#333' }}>Agregar Proveedor</h5>
              <CForm onSubmit={handleAgregar}>

                {/* Nombre */}
                <CFormLabel style={{ fontWeight: '600', color: '#333' }}>
                  Nombre
                </CFormLabel>
                <CInputGroup className="mb-3">
                  <CInputGroupText>
                    <CIcon icon={cilUser} />
                  </CInputGroupText>
                  <CFormInput
                    type="text"
                    name="nombre"
                    placeholder="Nombre del proveedor"
                    value={form.nombre}
                    onChange={handleChange}
                  />
                </CInputGroup>

                {/* Teléfono */}
                <CFormLabel style={{ fontWeight: '600', color: '#333' }}>
                  Teléfono
                </CFormLabel>
                <CInputGroup className="mb-3">
                  <CInputGroupText>
                    <CIcon icon={cilPhone} />
                  </CInputGroupText>
                  <CFormInput
                    type="text"
                    name="telefono"
                    placeholder="Número telefónico"
                    value={form.telefono}
                    onChange={handleChange}
                  />
                </CInputGroup>

                {/* Correo */}
                <CFormLabel style={{ fontWeight: '600', color: '#333' }}>
                  Correo
                </CFormLabel>
                <CInputGroup className="mb-3">
                  <CInputGroupText>
                    <CIcon icon={cilEnvelopeClosed} />
                  </CInputGroupText>
                  <CFormInput
                    type="email"
                    name="correo"
                    placeholder="Correo electrónico"
                    value={form.correo}
                    onChange={handleChange}
                  />
                </CInputGroup>

                <CButton
                  type="submit"
                  color="dark"
                  style={{
                    background: '#000',
                    width: '100%',
                    fontWeight: '600',
                  }}
                >
                  <CIcon icon={cilSave} className="me-2" />
                  Guardar
                </CButton>
              </CForm>
            </CCol>

            {/* TABLA */}
            <CCol md={8}>
              <h5 style={{ fontWeight: 'bold', color: '#333' }}>Lista de Proveedores</h5>
              <CTable hover responsive bordered>
                <CTableHead color="dark">
                  <CTableRow>
                    <CTableHeaderCell>Nombre</CTableHeaderCell>
                    <CTableHeaderCell>Teléfono</CTableHeaderCell>
                    <CTableHeaderCell>Correo</CTableHeaderCell>
                    <CTableHeaderCell className="text-center">Acciones</CTableHeaderCell>
                  </CTableRow>
                </CTableHead>
                <CTableBody>
                  {provedores.map((p) => (
                    <CTableRow key={p.id}>
                      <CTableDataCell>{p.nombre}</CTableDataCell>
                      <CTableDataCell>{p.telefono}</CTableDataCell>
                      <CTableDataCell>{p.correo}</CTableDataCell>
                      <CTableDataCell className="text-center">
                        <CButton
                          size="sm"
                          color="warning"
                          className="me-2 text-white"
                          onClick={() => abrirEditar(p)}
                        >
                          <CIcon icon={cilPen} />
                        </CButton>

                        <CButton
                          size="sm"
                          color="danger"
                          onClick={() => handleEliminar(p.id)}
                        >
                          <CIcon icon={cilTrash} />
                        </CButton>
                      </CTableDataCell>
                    </CTableRow>
                  ))}
                </CTableBody>
              </CTable>
            </CCol>
          </CRow>
        </CCardBody>
      </CCard>

      {/* MODAL EDITAR */}
      <CModal visible={editModal} onClose={() => setEditModal(false)}>
        <CModalHeader style={{ fontWeight: 'bold' }}>Editar Proveedor</CModalHeader>
        <CModalBody>
          {proveedorEditando && (
            <>
              {/* Nombre */}
              <CFormLabel>Nombre</CFormLabel>
              <CFormInput
                className="mb-3"
                value={proveedorEditando.nombre}
                onChange={(e) =>
                  setProveedorEditando({
                    ...proveedorEditando,
                    nombre: e.target.value,
                  })
                }
              />

              {/* Teléfono */}
              <CFormLabel>Teléfono</CFormLabel>
              <CFormInput
                className="mb-3"
                value={proveedorEditando.telefono}
                onChange={(e) =>
                  setProveedorEditando({
                    ...proveedorEditando,
                    telefono: e.target.value,
                  })
                }
              />

              {/* Correo */}
              <CFormLabel>Correo</CFormLabel>
              <CFormInput
                className="mb-3"
                value={proveedorEditando.correo}
                onChange={(e) =>
                  setProveedorEditando({
                    ...proveedorEditando,
                    correo: e.target.value,
                  })
                }
              />

              <CButton
                color="dark"
                style={{ background: '#000' }}
                onClick={handleGuardarEdicion}
              >
                <CIcon icon={cilSave} className="me-2" />
                Guardar cambios
              </CButton>
            </>
          )}
        </CModalBody>
      </CModal>
    </>
  )
}

export default Provedores
