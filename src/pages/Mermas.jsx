import React, { useState } from 'react'
import { CRow, CCol, CFormInput, CInputGroup, CInputGroupText, CWidgetStatsF } from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilSearch } from '@coreui/icons'

const Mermas = () => {
  const [busqueda, setBusqueda] = useState('')

  // Datos de ejemplo (puedes cambiarlos luego)
  const mermas = [
    { id: 1, nombre: 'Harina vencida', cantidad: '3 kg', color: 'danger', icono: '🥣' },
    { id: 2, nombre: 'Huevos rotos', cantidad: '12 unidades', color: 'warning', icono: '🥚' },
    { id: 3, nombre: 'Leche derramada', cantidad: '5 litros', color: 'info', icono: '🥛' },
  ]

  const mermasFiltradas = mermas.filter((m) =>
    m.nombre.toLowerCase().includes(busqueda.toLowerCase())
  )

  return (
    <>
      {/* Barra de búsqueda */}
      <CRow className="mb-4">
        <CCol xs={12} md={6} lg={4}>
          <CInputGroup>
            <CInputGroupText>
              <CIcon icon={cilSearch} />
            </CInputGroupText>
            <CFormInput
              type="text"
              placeholder="Buscar merma..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </CInputGroup>
        </CCol>
      </CRow>

      {/* Lista de mermas */}
      <CRow>
        {mermasFiltradas.map((merma) => (
          <CCol xs={12} sm={6} md={4} lg={3} key={merma.id}>
            <CWidgetStatsF
              className="mb-3"
              color={merma.color}
              title={
                <span style={{ fontSize: '1.1rem', fontWeight: '600' }}>
                  {merma.nombre}
                </span>
              }
              value={
                <span style={{ fontSize: '1rem', fontWeight: '500' }}>
                  {merma.cantidad}
                </span>
              }
              icon={<span style={{ fontSize: '24px' }}>{merma.icono}</span>}
              padding={false}
            />
          </CCol>
        ))}
      </CRow>

      {/* Si no hay resultados */}
      {mermasFiltradas.length === 0 && (
        <CRow>
          <CCol xs={12} className="text-center py-5">
            <p className="text-muted">
              No se encontraron mermas que coincidan con "{busqueda}"
            </p>
          </CCol>
        </CRow>
      )}
    </>
  )
}

export default Mermas
