import React, { useState, useEffect } from 'react'
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
import {
  cilUser,
  cilPhone,
  cilSave,
  cilTrash,
  cilPen,
} from '@coreui/icons'

const Provedores = () => {
  const [usuarios, setUsuarios] = useState([])
  const [repartidores, setRepartidores] = useState([])

  const [form, setForm] = useState({
    nombre: '',
    apellidos: '',
    telefono: '',
    usuarioId: '',
  })

  const [editModal, setEditModal] = useState(false)
  const [repartidorEditando, setRepartidorEditando] = useState(null)

  // ======== CARGAR USUARIOS (solo rol 2) ==========
  const fetchUsuarios = async () => {
    try {
      const res = await fetch('https://pizzahub-api.onrender.com/api/Usuarios')
      const data = await res.json()

      const filtrados = data.filter((u) => u.rol === 2 || u.rol === 3) // empleados o repartidores
      setUsuarios(filtrados)
    } catch (err) {
      console.error('Error cargando usuarios:', err)
    }
  }

  // ======== CARGAR REPARTIDORES ==========
  const fetchRepartidores = async () => {
    try {
      const res = await fetch('https://pizzahub-api.onrender.com/api/Repartidores')
      const data = await res.json()
      setRepartidores(data)
    } catch (err) {
      console.error('Error cargando repartidores:', err)
    }
  }

  useEffect(() => {
    fetchUsuarios()
    fetchRepartidores()
  }, [])

  // ========= MANEJAR FORM ==========
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  // ========= REGISTRAR REPARTIDOR ==========
  const handleAgregar = async (e) => {
    e.preventDefault()

    if (!form.nombre || !form.apellidos || !form.telefono || !form.usuarioId) return

    try {
      const res = await fetch('https://pizzahub-api.onrender.com/api/Repartidores', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })

      if (res.ok) {
        await fetchRepartidores()
        setForm({ nombre: '', apellidos: '', telefono: '', usuarioId: '' })
      }
    } catch (err) {
      console.error('Error registrando:', err)
    }
  }

  // ========= ELIMINAR ==========
  const handleEliminar = async (id) => {
    try {
      await fetch(`https://pizzahub-api.onrender.com/api/Repartidores/${id}`, {
        method: 'DELETE',
      })

      fetchRepartidores()
    } catch (err) {
      console.error('Error eliminando:', err)
    }
  }

  // ========= EDITAR ==========
  const abrirEditar = (rep) => {
    setRepartidorEditando(rep)
    setEditModal(true)
  }

  const handleGuardarEdicion = async () => {
    try {
      await fetch(`https://pizzahub-api.onrender.com/api/Repartidores/${repartidorEditando.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(repartidorEditando),
      })

      setEditModal(false)
      fetchRepartidores()
    } catch (err) {
      console.error('Error editando:', err)
    }
  }

  return (
    <>
      <CCard>
        <CCardHeader
          style={{
            background: '#2980B9',
            color: 'white',
            fontWeight: 'bold',
            fontSize: '1.2rem',
            letterSpacing: '1px',
          }}
        >
          Registro de Repartidores
        </CCardHeader>

        <CCardBody style={{ background: '#fafafa' }}>
          <CRow>
            {/* FORMULARIO */}
            <CCol md={4}>
              <h5 style={{ fontWeight: 'bold', color: '#333' }}>Nuevo Repartidor</h5>

              <CForm onSubmit={handleAgregar}>
                {/* Nombre */}
                <CFormLabel>Nombre</CFormLabel>
                <CInputGroup className="mb-3">
                  <CInputGroupText>
                    <CIcon icon={cilUser} />
                  </CInputGroupText>
                  <CFormInput
                    type="text"
                    name="nombre"
                    placeholder="Nombre"
                    value={form.nombre}
                    onChange={handleChange}
                  />
                </CInputGroup>

                {/* Apellidos */}
                <CFormLabel>Apellidos</CFormLabel>
                <CFormInput
                  className="mb-3"
                  type="text"
                  name="apellidos"
                  placeholder="Apellidos"
                  value={form.apellidos}
                  onChange={handleChange}
                />

                {/* Teléfono */}
                <CFormLabel>Teléfono</CFormLabel>
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

                {/* Usuario */}
                <CFormLabel>Usuario</CFormLabel>
                <CFormInput
                  list="usuariosList"
                  name="usuarioId"
                  className="mb-3"
                  placeholder="Selecciona un usuario"
                  value={form.usuarioId}
                  onChange={handleChange}
                />

                <datalist id="usuariosList">
                  {usuarios.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.nombreUsuario}
                    </option>
                  ))}
                </datalist>

                <CButton type="submit" color="dark" style={{ width: '100%' }}>
                  <CIcon icon={cilSave} className="me-2" />
                  Guardar
                </CButton>
              </CForm>
            </CCol>

            {/* TABLA */}
            <CCol md={8}>
              <h5 style={{ fontWeight: 'bold', color: '#333' }}>Lista de Repartidores</h5>

              <CTable hover bordered responsive>
                <CTableHead color="dark">
                  <CTableRow>
                    <CTableHeaderCell>Nombre</CTableHeaderCell>
                    <CTableHeaderCell>Teléfono</CTableHeaderCell>
                    <CTableHeaderCell>Usuario</CTableHeaderCell>
                    <CTableHeaderCell className="text-center">Acciones</CTableHeaderCell>
                  </CTableRow>
                </CTableHead>

                <CTableBody>
                  {repartidores.map((r) => (
                    <CTableRow key={r.id}>
                      <CTableDataCell>{r.nombre + ' ' + r.apellidos}</CTableDataCell>
                      <CTableDataCell>{r.telefono}</CTableDataCell>
                      <CTableDataCell>{r.usuario?.nombreUsuario || '-'}</CTableDataCell>

                      <CTableDataCell className="text-center">
                        <CButton
                          size="sm"
                          color="warning"
                          className="me-2 text-white"
                          onClick={() => abrirEditar(r)}
                        >
                          <CIcon icon={cilPen} />
                        </CButton>

                        <CButton
                          size="sm"
                          color="danger"
                          onClick={() => handleEliminar(r.id)}
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
        <CModalHeader>Editar Repartidor</CModalHeader>
        <CModalBody>
          {repartidorEditando && (
            <>
              <CFormLabel>Nombre</CFormLabel>
              <CFormInput
                className="mb-3"
                value={repartidorEditando.nombre}
                onChange={(e) =>
                  setRepartidorEditando({
                    ...repartidorEditando,
                    nombre: e.target.value,
                  })
                }
              />

              <CFormLabel>Apellidos</CFormLabel>
              <CFormInput
                className="mb-3"
                value={repartidorEditando.apellidos}
                onChange={(e) =>
                  setRepartidorEditando({
                    ...repartidorEditando,
                    apellidos: e.target.value,
                  })
                }
              />

              <CFormLabel>Teléfono</CFormLabel>
              <CFormInput
                className="mb-3"
                value={repartidorEditando.telefono}
                onChange={(e) =>
                  setRepartidorEditando({
                    ...repartidorEditando,
                    telefono: e.target.value,
                  })
                }
              />

              <CButton color="dark" onClick={handleGuardarEdicion}>
                <CIcon icon={cilSave} className="me-2" />
                Guardar Cambios
              </CButton>
            </>
          )}
        </CModalBody>
      </CModal>
    </>
  )
}

export default Provedores
