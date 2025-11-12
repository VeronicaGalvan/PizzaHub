import React from 'react'
import { Link } from 'react-router-dom'
import {
  CButton,
  CCard,
  CCardBody,
  CCardGroup,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CInputGroup,
  CInputGroupText,
  CRow,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilLockLocked, cilUser } from '@coreui/icons'

const Login = ({ onSuccess }) => {
  return (
    <div
      className="min-vh-100 d-flex flex-row align-items-center"
      style={{
        backgroundImage: `linear-gradient(rgba(255, 140, 0, 0.4), rgba(255, 140, 0, 0.4)), url('https://images.unsplash.com/photo-1565299507177-b0ac66763828?auto=format&fit=crop&w=1600&q=80')`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
      }}
    >
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={8}>
            <CCardGroup>
              
              {/* IZQUIERDA: Formulario */}
              <CCard className="p-4">
                <CCardBody>
                  <CForm>
                    <h1>Iniciar Sesión</h1>
                    <p className="text-body-secondary">
                      Accede con tus credenciales
                    </p>

                    {/* Usuario */}
                    <CInputGroup className="mb-3">
                      <CInputGroupText>
                        <CIcon icon={cilUser} />
                      </CInputGroupText>
                      <CFormInput
                        placeholder="Usuario"
                        autoComplete="username"
                      />
                    </CInputGroup>

                    {/* Contraseña */}
                    <CInputGroup className="mb-4">
                      <CInputGroupText>
                        <CIcon icon={cilLockLocked} />
                      </CInputGroupText>
                      <CFormInput
                        type="password"
                        placeholder="Contraseña"
                        autoComplete="current-password"
                      />
                    </CInputGroup>

                    <CRow>
                      <CCol xs={6}>
                        <CButton
                          style={{
                            backgroundColor: '#ff8c00',
                            borderColor: '#ff8c00',
                          }}
                          className="px-4"
                          onClick={() => onSuccess?.()}
                        >
                          Entrar
                        </CButton>
                      </CCol>
                      <CCol xs={6} className="text-right">
                        <CButton color="link" className="px-0">
                          ¿Olvidaste tu contraseña?
                        </CButton>
                      </CCol>
                    </CRow>
                  </CForm>
                </CCardBody>
              </CCard>

              {/* DERECHA: Presentación */}
              <CCard
                className="text-white py-5"
                style={{
                  width: '44%',
                  backgroundImage: `linear-gradient(rgba(10, 10, 10, 0.7), rgba(243, 145, 26, 0.7)), url('https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1600&q=80')`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                  border: 'none',
                }}
              >
                <CCardBody className="text-center d-flex flex-column align-items-center justify-content-center">
                  
                  {/* Logo circular */}
                  <div style={{ width: '150px', height: '150px', marginBottom: '20px' }}>
                    <img
                      src="/logoPizza.jpg"
                      alt="Logo PizzaHub"
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                        borderRadius: '50%',
                        border: '4px solid #ff8c00',
                        boxShadow: '0 4px 10px rgba(0,0,0,0.3)',
                      }}
                    />
                  </div>

                  <h2>Bienvenido</h2>
                  <p style={{ color: '#fff', maxWidth: '250px', margin: '10px auto' }}>
                    Administra pedidos, usuarios, inventarios y proveedores desde un solo panel.
                  </p>

                  <Link to="/register">
                    <CButton
                      style={{
                        backgroundColor: '#ff8c00',
                        borderColor: '#ff8c00',
                      }}
                      className="mt-3"
                      active
                      tabIndex={-1}
                    >
                      Crear cuenta
                    </CButton>
                  </Link>

                </CCardBody>
              </CCard>

            </CCardGroup>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  )
}

export default Login
