import React, { useState } from 'react'
import {
  CCard,
  CCardHeader,
  CCardBody,
  CRow,
  CCol,
  CButton,
  CTable,
  CTableHead,
  CTableRow,
  CTableHeaderCell,
  CTableBody,
  CTableDataCell,
  CForm,
  CFormInput,
  CFormLabel,
  CFormSelect,
  CModal,
  CModalHeader,
  CModalBody,
  CModalFooter,
  CInputGroup,
  CInputGroupText,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilPen, cilTrash, cilPlus,  cilCheckCircle, cilXCircle } from '@coreui/icons'

const Provedores = () => {
  const [provedores, setProvedores] = useState([
    {
      id: 1,
      personaId: 101,
      sobrenombre: 'Distribuidora México',
      vehiculoAsignado: 'Harinas y Azúcares',
      horarioTrabajo: '9AM - 5PM',
      disponible: true,
      activo: true,
    },
    {
      id: 2,
      personaId: 102,
      sobrenombre: 'Abarrotes San Juan',
      vehiculoAsignado: 'Lácteos',
      horarioTrabajo: '7AM - 2PM',
      disponible: false,
      activo: true,
    },
  ])

  const [modalAgregar, setModalAgregar] = useState(false)
  const [modalEditar, setModalEditar] = useState(false)

  const [form, setForm] = useState({
    personaId: '',
    sobrenombre: '',
    vehiculoAsignado: '',
    horarioTrabajo: '',
    disponible: false,
    activo: true,
  })

  const [editarProveedor, setEditarProveedor] = useState(null)

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setForm({
      ...form,
      [name]: type === 'checkbox' ? checked : value,
    })
  }

  // AGREGAR
  const agregarProveedor = (e) => {
    e.preventDefault()

    const nuevo = {
      id: Date.now(),
      ...form,
    }

    setProvedores([...provedores, nuevo])
    setModalAgregar(false)

    setForm({
      personaId: '',
      sobrenombre: '',
      vehiculoAsignado: '',
      horarioTrabajo: '',
      disponible: false,
      activo: true,
    })
  }

  // EDITAR
  const abrirEditar = (p) => {
    setEditarProveedor(p)
    setModalEditar(true)
  }

  const guardarEdicion = () => {
    setProvedores(
      provedores.map((p) =>
        p.id === editarProveedor.id ? editarProveedor : p
      )
    )
    setModalEditar(false)
  }

  // ELIMINAR
  const eliminarProveedor = (id) => {
    setProvedores(provedores.filter((p) => p.id !== id))
  }

  return (
    <>
      <CCard>
        <CCardHeader
          style={{
            background: '#D35400',
            color: 'white',
            fontWeight: 'bold',
            fontSize: '1.2rem',
          }}
        >
          Gestión de Proveedores
        </CCardHeader>

        <CCardBody style={{ background: '#fafafa' }}>

          <div className="d-flex justify-content-end mb-3">
            <CButton
              color="dark"
              style={{ background: '#000', fontWeight: '600' }}
              onClick={() => setModalAgregar(true)}
            >
              <CIcon icon={cilPlus} className="me-2" />
              Agregar Proveedor
            </CButton>
          </div>

          {/* TABLA */}
          <CTable bordered hover responsive>
            <CTableHead color="dark">
              <CTableRow>
                <CTableHeaderCell>ID</CTableHeaderCell>
                <CTableHeaderCell>Nombre Comercial</CTableHeaderCell>
                <CTableHeaderCell>Rubro</CTableHeaderCell>
                <CTableHeaderCell>Horario</CTableHeaderCell>
                <CTableHeaderCell>Disponible</CTableHeaderCell>
                <CTableHeaderCell>Activo</CTableHeaderCell>
                <CTableHeaderCell className="text-center">Acciones</CTableHeaderCell>
              </CTableRow>
            </CTableHead>

            <CTableBody>
              {provedores.map((p) => (
                <CTableRow key={p.id}>
                  <CTableDataCell>{p.personaId}</CTableDataCell>
                  <CTableDataCell>{p.sobrenombre}</CTableDataCell>
                  <CTableDataCell>{p.vehiculoAsignado}</CTableDataCell>
                  <CTableDataCell>{p.horarioTrabajo}</CTableDataCell>

                  {/* DISPONIBLE */}
                  <CTableDataCell>
                    {p.disponible ? (
                      <span style={{ color: 'green', fontWeight: '600' }}>
                        <CIcon icon={cilCheckCircle} className="me-1" />
                        Sí
                      </span>
                    ) : (
                      <span style={{ color: 'red', fontWeight: '600' }}>
                        <CIcon icon={cilXCircle} className="me-1" />
                        No
                      </span>
                    )}
                  </CTableDataCell>

                  {/* ACTIVO */}
                  <CTableDataCell>
                    {p.activo ? (
                      <span style={{ color: 'green', fontWeight: '600' }}>Activo</span>
                    ) : (
                      <span style={{ color: 'red', fontWeight: '600' }}>Inactivo</span>
                    )}
                  </CTableDataCell>

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
                      onClick={() => eliminarProveedor(p.id)}
                    >
                      <CIcon icon={cilTrash} />
                    </CButton>
                  </CTableDataCell>
                </CTableRow>
              ))}
            </CTableBody>
          </CTable>
        </CCardBody>
      </CCard>

      {/* MODAL AGREGAR */}
      <CModal visible={modalAgregar} onClose={() => setModalAgregar(false)}>
        <CModalHeader>Agregar Proveedor</CModalHeader>
        <CModalBody>
          <CForm onSubmit={agregarProveedor}>

            <CFormLabel>ID Interno</CFormLabel>
            <CFormInput
              name="personaId"
              className="mb-3"
              type="number"
              value={form.personaId}
              onChange={handleChange}
            />

            <CFormLabel>Nombre Comercial</CFormLabel>
            <CFormInput
              name="sobrenombre"
              className="mb-3"
              value={form.sobrenombre}
              onChange={handleChange}
            />

            <CFormLabel>Rubro</CFormLabel>
            <CFormInput
              name="vehiculoAsignado"
              className="mb-3"
              value={form.vehiculoAsignado}
              onChange={handleChange}
            />

            <CFormLabel>Horario de Trabajo</CFormLabel>
            <CFormInput
              name="horarioTrabajo"
              className="mb-3"
              value={form.horarioTrabajo}
              onChange={handleChange}
            />

            <CFormLabel>Disponible</CFormLabel>
            <CFormSelect
              name="disponible"
              className="mb-3"
              value={form.disponible}
              onChange={(e) =>
                setForm({ ...form, disponible: e.target.value === 'true' })
              }
            >
              <option value="false">No</option>
              <option value="true">Sí</option>
            </CFormSelect>

            <CButton type="submit" color="dark" style={{ background: '#000' }}>
              Guardar
            </CButton>
          </CForm>
        </CModalBody>
      </CModal>

      {/* MODAL EDITAR */}
      <CModal visible={modalEditar} onClose={() => setModalEditar(false)}>
        <CModalHeader>Editar Proveedor</CModalHeader>

        <CModalBody>
          {editarProveedor && (
            <>
              <CFormLabel>Nombre Comercial</CFormLabel>
              <CFormInput
                className="mb-3"
                value={editarProveedor.sobrenombre}
                onChange={(e) =>
                  setEditarProveedor({ ...editarProveedor, sobrenombre: e.target.value })
                }
              />

              <CFormLabel>Rubro</CFormLabel>
              <CFormInput
                className="mb-3"
                value={editarProveedor.vehiculoAsignado}
                onChange={(e) =>
                  setEditarProveedor({ ...editarProveedor, vehiculoAsignado: e.target.value })
                }
              />

              <CFormLabel>Horario Trabajo</CFormLabel>
              <CFormInput
                className="mb-3"
                value={editarProveedor.horarioTrabajo}
                onChange={(e) =>
                  setEditarProveedor({ ...editarProveedor, horarioTrabajo: e.target.value })
                }
              />

              <CFormLabel>Disponible</CFormLabel>
              <CFormSelect
                className="mb-3"
                value={editarProveedor.disponible}
                onChange={(e) =>
                  setEditarProveedor({
                    ...editarProveedor,
                    disponible: e.target.value === 'true',
                  })
                }
              >
                <option value="true">Sí</option>
                <option value="false">No</option>
              </CFormSelect>

              <CButton color="dark" style={{ background: '#000' }} onClick={guardarEdicion}>
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
