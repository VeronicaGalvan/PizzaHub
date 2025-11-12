import React from 'react'
import { NavLink } from 'react-router-dom'
import PropTypes from 'prop-types'

import SimpleBar from 'simplebar-react'
import 'simplebar-react/dist/simplebar.min.css'

import { CBadge, CNavLink, CSidebarNav } from '@coreui/react'

export const AppSidebarNav = ({ items }) => {

  // ESTILOS PARA EL ITEM ACTIVO (SIN ARCHIVO CSS)
  const activeStyles = `
    .nav-link.active {
      color: #ffb984 !important;
      font-weight: bold !important;
      background-color: rgba(255, 255, 255, 0.25) !important;
      border-radius: 6px;
    }
    .nav-link.active .nav-icon {
      color: #ffb984 !important;
    }
  `

  const navLink = (name, icon, badge, indent = false) => {
    return (
      <>
        {icon
          ? icon
          : indent && (
              <span className="nav-icon">
                <span className="nav-icon-bullet"></span>
              </span>
            )}

        {/* Color aplicado directamente al texto */}
        {name && (
          <span style={{ color: '#000000', fontWeight: '500' }}>
            {name}
          </span>
        )}

        {badge && (
          <CBadge color={badge.color} className="ms-auto" size="sm">
            {badge.text}
          </CBadge>
        )}
      </>
    )
  }

  const navItem = (item, index, indent = false) => {
    const { component, name, badge, icon, ...rest } = item
    const Component = component

    // TITULOS
    if (Component.displayName === 'CNavTitle' || Component.name === 'CNavTitle') {
      return (
        <Component
          key={index}
          {...rest}
          style={{
            color: '#000',
            fontWeight: 'bold',
            opacity: 1,
            borderBottom: '1px solid white',
            paddingBottom: '4px',
            marginBottom: '6px',
          }}
        >
          {name}
        </Component>
      )
    }

    // ITEMS NORMALES
    return (
      <Component as="div" key={index}>
        {rest.to || rest.href ? (
          <CNavLink
            {...(rest.to && { as: NavLink })}
            {...(rest.href && { target: '_blank', rel: 'noopener noreferrer' })}
            {...rest}
          >
            {navLink(name, icon, badge, indent)}
          </CNavLink>
        ) : (
          navLink(name, icon, badge, indent)
        )}
      </Component>
    )
  }

  const navGroup = (item, index) => {
    const { component, name, icon, items, to, ...rest } = item
    const Component = component
    return (
      <Component compact as="div" key={index} toggler={navLink(name, icon)} {...rest}>
        {items?.map((item, index) =>
          item.items ? navGroup(item, index) : navItem(item, index, true),
        )}
      </Component>
    )
  }

  return (
    <>
      {/* INYECCIÓN DE ESTILOS PARA EL ITEM ACTIVO */}
      <style>{activeStyles}</style>

      <CSidebarNav as={SimpleBar}>
        {items &&
          items.map((item, index) =>
            item.items ? navGroup(item, index) : navItem(item, index),
          )}
      </CSidebarNav>
    </>
  )
}

AppSidebarNav.propTypes = {
  items: PropTypes.arrayOf(PropTypes.any).isRequired,
}
