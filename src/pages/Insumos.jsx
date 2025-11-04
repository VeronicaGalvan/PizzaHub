import React, { useState } from 'react'
import CIcon from '@coreui/icons-react'
import { cilPlus, cilSearch } from '@coreui/icons'
import { CCol, CRow, CWidgetStatsF, CFormInput, CInputGroup, CInputGroupText } from '@coreui/react'

const Insumos = () => {
  const [busqueda, setBusqueda] = useState('')

  // Ejemplo de insumos con diferentes tipos de iconos
  const insumos = [
    {
      id: 1,
      nombre: 'Harina',
      cantidad: '50 kg',
      color: 'primary',
      imagen: '🌾',
      tipoIcono: 'emoji'
    },
    {
      id: 2,
      nombre: 'Azúcar',
      cantidad: '30 kg',
      color: 'info',
      imagen: '🍬',
      tipoIcono: 'emoji'
    },
    {
      id: 3,
      nombre: 'Huevos',
      cantidad: '600 unidades',
      color: 'success',
      imagen: '🥚',
      tipoIcono: 'emoji'
    },
    {
      id: 4,
      nombre: 'Mantequilla',
      cantidad: '20 kg',
      color: 'warning',
      imagen: '🧈',
      tipoIcono: 'emoji'
    },
    {
      id: 5,
      nombre: 'Leche',
      cantidad: '60 litros',
      color: 'secondary',
      imagen: '🥛',
      tipoIcono: 'emoji'
    },
    {
      id: 6,
      nombre: 'Chocolate',
      cantidad: '25 kg',
      color: 'danger',
      imagen: '🍫',
      tipoIcono: 'emoji'
    },
    {
      id: 7,
      nombre: 'Vainilla',
      cantidad: '8 litros',
      color: 'dark',
      imagen: '🌼',
      tipoIcono: 'emoji'
    },
    {
      id: 8,
      nombre: 'Sal',
      cantidad: '10 kg',
      color: 'primary',
      imagen: '🧂',
      tipoIcono: 'emoji'
    },
    {
      id: 9,
      nombre: 'Aceite vegetal',
      cantidad: '40 litros',
      color: 'info',
      imagen: '🫙',
      tipoIcono: 'emoji'
    },
    {
      id: 10,
      nombre: 'Avena',
      cantidad: '15 kg',
      color: 'success',
      imagen: '🌾',
      tipoIcono: 'emoji'
    },
    {
      id: 11,
      nombre: 'Canela',
      cantidad: '5 kg',
      color: 'warning',
      imagen: '🌿',
      tipoIcono: 'emoji'
    },
    {
      id: 12,
      nombre: 'Miel',
      cantidad: '12 litros',
      color: 'secondary',
      imagen: '🍯',
      tipoIcono: 'emoji'
    },
    {
      id: 13,
      nombre: 'Nueces',
      cantidad: '8 kg',
      color: 'danger',
      imagen: '🥜',
      tipoIcono: 'emoji'
    },
    {
      id: 14,
      nombre: 'Polvo para hornear',
      cantidad: '3 kg',
      color: 'dark',
      imagen: '🥄',
      tipoIcono: 'emoji'
    },
  ]

  // Filtrar insumos según la búsqueda
  const insumosFiltrados = insumos.filter((insumo) =>
    insumo.nombre.toLowerCase().includes(busqueda.toLowerCase())
  )

  const renderIcono = (insumo) => {
    if (insumo.tipoIcono === 'emoji') {
      return <span style={{ fontSize: '24px' }}>{insumo.imagen}</span>
    } else if (insumo.tipoIcono === 'imagen') {
      return <img src={insumo.imagen} alt={insumo.nombre} style={{ width: '24px', height: '24px' }} />
    } else if (insumo.tipoIcono === 'icono') {
      return <CIcon icon={insumo.imagen} height={24} />
    }
    return null
  }

  const handleInsumoClick = (insumo) => {
    // Aquí puedes agregar la lógica para navegar al detalle del insumo
    console.log('Ver detalle del insumo:', insumo)
    // Por ejemplo: navigate(`/insumos/${insumo.id}`)
    // O abrir un modal con los detalles
  }

  const handleAgregarInsumo = () => {
    // Lógica para agregar nuevo insumo
    console.log('Agregar nuevo insumo')
    // Por ejemplo: navigate('/insumos/nuevo')
    // O abrir modal de formulario
  }

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
              placeholder="Buscar insumo..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </CInputGroup>
        </CCol>
      </CRow>

      {/* Grid de insumos */}
      <CRow>
        {insumosFiltrados.map((insumo) => (
          <CCol xs={12} sm={6} md={4} lg={3} key={insumo.id}>
            <CWidgetStatsF
              className="mb-3"
              style={{ 
                minHeight: '120px',
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s'
              }}
              color={insumo.color}
              icon={renderIcono(insumo)}
              title={
                <span style={{ fontSize: '1.1rem', fontWeight: '600' }}>
                  {insumo.nombre}
                </span>
              }
              value={
                <span style={{ fontSize: '1rem', fontWeight: '500' }}>
                  {insumo.cantidad}
                </span>
              }
              padding={false}
              onClick={() => handleInsumoClick(insumo)}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-4px)'
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)'
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)'
                e.currentTarget.style.boxShadow = ''
              }}
            />
          </CCol>
        ))}

        {/* Tarjeta para agregar nuevo insumo */}
        <CCol xs={12} sm={6} md={4} lg={3}>
          <CWidgetStatsF
            className="mb-3"
            style={{
              minHeight: '120px',
              cursor: 'pointer',
              border: '2px dashed #d8dbe0',
              transition: 'transform 0.2s, border-color 0.2s'
            }}
            color="light"
            icon={<CIcon icon={cilPlus} height={24} />}
            title={
              <span style={{ fontSize: '1.1rem', fontWeight: '600' }}>
                Agregar Insumo
              </span>
            }
            value=""
            padding={false}
            onClick={handleAgregarInsumo}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-4px)'
              e.currentTarget.style.borderColor = '#999'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)'
              e.currentTarget.style.borderColor = '#d8dbe0'
            }}
          />
        </CCol>
      </CRow>

      {/* Mensaje cuando no hay resultados */}
      {insumosFiltrados.length === 0 && (
        <CRow>
          <CCol xs={12} className="text-center py-5">
            <p className="text-muted">No se encontraron insumos que coincidan con "{busqueda}"</p>
          </CCol>
        </CRow>
      )}
    </>
  )
}

export default Insumos