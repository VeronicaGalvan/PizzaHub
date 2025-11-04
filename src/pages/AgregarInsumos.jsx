import React, { useState } from 'react'
import {
  CCol,
  CRow,
  CButton,
  CForm,
  CFormInput,
  CFormLabel,
  CFormSelect,
  CFormTextarea,
  CCard,
  CCardBody,
  CCardHeader,
} from '@coreui/react'

const AgregarInsumos = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    cantidad: '',
    unidad: '',
    proveedor: '',
    caducidad: '',
    costo: '',
    tipoIcono: 'emoji',
    imagen: '',
    color: 'primary',
    notas: ''
  })

  const coloresDisponibles = [
    { value: 'primary', label: 'Azul' },
    { value: 'secondary', label: 'Gris' },
    { value: 'success', label: 'Verde' },
    { value: 'danger', label: 'Rojo' },
    { value: 'warning', label: 'Amarillo' },
    { value: 'info', label: 'Cian' },
    { value: 'dark', label: 'Negro' }
  ]

  const emojisComunes = ['🌾', '🍬', '🥚', '🧈', '🥛', '🍫', '🌼', '🧂', '🫙', '🌿', '🍯', '🥜', '🥄', '🍞', '🥖', '🧁', '🍰', '☕', '🧃', '🥤']

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    console.log('Datos del insumo:', formData)
    // Aquí puedes agregar la lógica para guardar el insumo
    // Por ejemplo: enviar a una API, guardar en estado global, etc.
    alert('Insumo registrado correctamente')
  }

  const handleCancel = () => {
    // Lógica para cancelar y volver atrás
    console.log('Cancelar registro')
    // Por ejemplo: navigate('/insumos')
  }

  return (
    <CCard className="shadow-sm">
      <CCardHeader style={{ 
        background: 'linear-gradient(135deg, #f9b115 0%, #b95300ff 100%)',
        color: 'white',
        borderBottom: 'none'
      }}>
        <h4 className="mb-0">Registrar Nuevo Insumo</h4>
      </CCardHeader>
      <CCardBody style={{ backgroundColor: '#fafbfc' }}>
        <CForm onSubmit={handleSubmit}>
          <CRow className="g-3">
            {/* Nombre del Insumo */}
            <CCol md={6}>
              <CFormLabel htmlFor="nombre">Nombre del Insumo</CFormLabel>
              <CFormInput
                type="text"
                id="nombre"
                name="nombre"
                placeholder="Ej: Harina de trigo"
                value={formData.nombre}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Cantidad */}
            <CCol md={3}>
              <CFormLabel htmlFor="cantidad">Cantidad</CFormLabel>
              <CFormInput
                type="number"
                id="cantidad"
                name="cantidad"
                placeholder="Ej: 50"
                value={formData.cantidad}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Unidad de Medida */}
            <CCol md={3}>
              <CFormLabel htmlFor="unidad">Unidad</CFormLabel>
              <CFormSelect
                id="unidad"
                name="unidad"
                value={formData.unidad}
                onChange={handleInputChange}
              >
                <option value="">Seleccionar...</option>
                <option value="kg">Kilogramos (kg)</option>
                <option value="g">Gramos (g)</option>
                <option value="litros">Litros</option>
                <option value="ml">Mililitros (ml)</option>
                <option value="unidades">Unidades</option>
                <option value="piezas">Piezas</option>
                <option value="cajas">Cajas</option>
                <option value="paquetes">Paquetes</option>
              </CFormSelect>
            </CCol>

            {/* Proveedor */}
            <CCol md={6}>
              <CFormLabel htmlFor="proveedor">Proveedor</CFormLabel>
              <CFormInput
                type="text"
                id="proveedor"
                name="proveedor"
                placeholder="Nombre del proveedor"
                value={formData.proveedor}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Fecha de Caducidad */}
            <CCol md={3}>
              <CFormLabel htmlFor="caducidad">Fecha de Caducidad</CFormLabel>
              <CFormInput
                type="date"
                id="caducidad"
                name="caducidad"
                value={formData.caducidad}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Costo Unitario */}
            <CCol md={3}>
              <CFormLabel htmlFor="costo">Costo Unitario</CFormLabel>
              <CFormInput
                type="number"
                step="0.01"
                id="costo"
                name="costo"
                placeholder="$0.00"
                value={formData.costo}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Separador visual */}
            <CCol xs={12}>
              <hr className="my-3" />
              <h5>Personalización Visual</h5>
            </CCol>

            {/* Tipo de Icono */}
            <CCol md={4}>
              <CFormLabel htmlFor="tipoIcono">Tipo de Icono</CFormLabel>
              <CFormSelect
                id="tipoIcono"
                name="tipoIcono"
                value={formData.tipoIcono}
                onChange={handleInputChange}
              >
                <option value="emoji">Emoji</option>
                <option value="imagen">URL de Imagen</option>
              </CFormSelect>
            </CCol>

            {/* Imagen/Emoji */}
            {formData.tipoIcono === 'emoji' ? (
              <CCol md={4}>
                <CFormLabel htmlFor="imagen">Seleccionar Emoji</CFormLabel>
                <CFormSelect
                  id="imagen"
                  name="imagen"
                  value={formData.imagen}
                  onChange={handleInputChange}
                >
                  <option value="">Seleccionar emoji...</option>
                  {emojisComunes.map((emoji, index) => (
                    <option key={index} value={emoji}>
                      {emoji} Emoji {index + 1}
                    </option>
                  ))}
                </CFormSelect>
              </CCol>
            ) : (
              <CCol md={4}>
                <CFormLabel htmlFor="imagen">URL de la Imagen</CFormLabel>
                <CFormInput
                  type="text"
                  id="imagen"
                  name="imagen"
                  placeholder="https://ejemplo.com/imagen.jpg"
                  value={formData.imagen}
                  onChange={handleInputChange}
                />
              </CCol>
            )}

            {/* Color */}
            <CCol md={4}>
              <CFormLabel htmlFor="color">Color de la Tarjeta</CFormLabel>
              <CFormSelect
                id="color"
                name="color"
                value={formData.color}
                onChange={handleInputChange}
              >
                {coloresDisponibles.map((color) => (
                  <option key={color.value} value={color.value}>
                    {color.label}
                  </option>
                ))}
              </CFormSelect>
            </CCol>

            {/* Vista Previa */}
            <CCol xs={12}>
              <CFormLabel>Vista Previa</CFormLabel>
              <div
                style={{
                  padding: '20px',
                  borderRadius: '8px',
                  backgroundColor: '#f8f9fa',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '15px'
                }}
              >
                <div style={{ fontSize: '40px' }}>
                  {formData.tipoIcono === 'emoji' && formData.imagen ? (
                    formData.imagen
                  ) : formData.tipoIcono === 'imagen' && formData.imagen ? (
                    <img src={formData.imagen} alt="preview" style={{ width: '40px', height: '40px' }} />
                  ) : (
                    '❓'
                  )}
                </div>
                <div>
                  <div style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>
                    {formData.nombre || 'Nombre del insumo'}
                  </div>
                  <div style={{ color: '#666' }}>
                    {formData.cantidad && formData.unidad 
                      ? `${formData.cantidad} ${formData.unidad}` 
                      : 'Cantidad'}
                  </div>
                  <div style={{ fontSize: '0.9rem', color: '#888', marginTop: '5px' }}>
                    Color: <span className={`badge bg-${formData.color}`}>●</span> {coloresDisponibles.find(c => c.value === formData.color)?.label}
                  </div>
                </div>
              </div>
            </CCol>

            {/* Notas Adicionales */}
            <CCol xs={12}>
              <CFormLabel htmlFor="notas">Notas Adicionales (Opcional)</CFormLabel>
              <CFormTextarea
                id="notas"
                name="notas"
                rows="3"
                placeholder="Información adicional sobre el insumo..."
                value={formData.notas}
                onChange={handleInputChange}
              />
            </CCol>

            {/* Botones de Acción */}
            <CCol xs={12}>
              <div className="d-flex gap-2 justify-content-end mt-3">
                <CButton 
                  color="secondary" 
                  onClick={handleCancel}
                >
                  Cancelar
                </CButton>
                <CButton 
                  color="primary" 
                  type="submit"
                >
                  Registrar Insumo
                </CButton>
              </div>
            </CCol>
          </CRow>
        </CForm>
      </CCardBody>
    </CCard>
  )
}

export default AgregarInsumos