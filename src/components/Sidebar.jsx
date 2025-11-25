import React, { useContext } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import {
  CCloseButton,
  CSidebar,
  CSidebarBrand,
  CSidebarFooter,
  CSidebarHeader,
  CSidebarToggler,
  CButton
} from '@coreui/react'

import { AppSidebarNav } from './SidebarNav'
import navigation from '../navegacion/_nav'
import AuthContext from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

const AppSidebar = () => {
  const dispatch = useDispatch()
  const unfoldable = useSelector((state) => state.sidebarUnfoldable)
  const sidebarShow = useSelector((state) => state.sidebarShow)

  const { logout } = useContext(AuthContext)
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <CSidebar
      className="border-end"
      position="fixed"
      unfoldable={unfoldable}
      visible={sidebarShow}
      onVisibleChange={(visible) => {
        dispatch({ type: 'set', sidebarShow: visible })
      }}
      style={{
        background: 'linear-gradient(135deg, #ff8c00 0%, #ff6b00 50%, #ff4500 100%)',
        opacity: 1,
        padding: '10px 0',
        boxShadow: '4px 0 15px rgba(255, 69, 0, 0.2)',
      }}
    >
      {/* Header */}
      <CSidebarHeader
        className="border-bottom d-flex align-items-center justify-content-center"
        style={{
          backgroundColor: 'rgba(0, 0, 0, 0.15)',
          padding: '20px 0',
          borderBottom: '2px solid rgba(255, 255, 255, 0.2)',
        }}
      >
        <h2
          style={{
            color: '#ffffff',
            fontWeight: '900',
            letterSpacing: '3px',
            fontSize: '1.8rem',
            fontFamily: "'Bebas Neue', 'Arial Black', sans-serif",
            textShadow: '2px 2px 8px rgba(0, 0, 0, 0.3)',
            margin: 0,
            textTransform: 'uppercase',
          }}
        >
          PIZZAHUB
        </h2>

        <CSidebarBrand to="/" />

        <CCloseButton
          className="d-lg-none"
          style={{ color: '#fff' }}
          onClick={() => dispatch({ type: 'set', sidebarShow: false })}
        />
      </CSidebarHeader>

      {/* Navegación */}
      <AppSidebarNav items={navigation} />

      {/* Footer con Logout */}
      <CSidebarFooter
        className="border-top d-flex flex-column align-items-center justify-content-center p-3"
        style={{
          borderTop: '2px solid rgba(255, 255, 255, 0.2)',
          backgroundColor: 'rgba(0, 0, 0, 0.1)',
        }}
      >
        <CButton
          color="danger"
          style={{
            width: '100%',
            fontWeight: 'bold',
            backgroundColor: '#dc3545',
            borderColor: '#dc3545',
            borderRadius: '8px',
            padding: '10px',
            fontSize: '14px',
            boxShadow: '0 4px 10px rgba(220, 53, 69, 0.3)',
            transition: 'all 0.3s ease',
          }}
          onMouseEnter={(e) => {
            e.target.style.backgroundColor = '#c82333'
            e.target.style.transform = 'translateY(-2px)'
            e.target.style.boxShadow = '0 6px 15px rgba(220, 53, 69, 0.4)'
          }}
          onMouseLeave={(e) => {
            e.target.style.backgroundColor = '#dc3545'
            e.target.style.transform = 'translateY(0)'
            e.target.style.boxShadow = '0 4px 10px rgba(220, 53, 69, 0.3)'
          }}
          onClick={handleLogout}
        >
          🔒 Cerrar sesión
        </CButton>

        <CSidebarToggler
          className="mt-3"
          style={{
            backgroundColor: 'rgba(255, 255, 255, 0.2)',
            borderRadius: '6px',
          }}
          onClick={() => dispatch({ type: 'set', sidebarUnfoldable: !unfoldable })}
        />
      </CSidebarFooter>
    </CSidebar>
  )
}

export default React.memo(AppSidebar)