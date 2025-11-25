
// ========== SidebarNav.jsx ==========
import React from 'react'
import { NavLink } from 'react-router-dom'
import PropTypes from 'prop-types'

import SimpleBar from 'simplebar-react'
import 'simplebar-react/dist/simplebar.min.css'

import { CBadge, CNavLink, CSidebarNav } from '@coreui/react'

export const AppSidebarNav = ({ items }) => {

  const activeStyles = `
    .nav-link.active {
      color: #ffffff !important;
      font-weight: bold !important;
      background-color: rgba(255, 255, 255, 0.25) !important;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    }
    .nav-link.active .nav-icon {
      color: #ffffff !important;
    }
    .nav-link:hover {
      background-color: rgba(255, 255, 255, 0.15) !important;
      border-radius: 8px;
      transition: all 0.2s ease;
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

        {name && (
          <span style={{ 
            color: '#ffffff', 
            fontWeight: '500',
            fontSize: '14px',
          }}>
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
            color: '#ffffff',
            fontWeight: '800',
            fontSize: '12px',
            letterSpacing: '1.5px',
            opacity: 0.9,
            borderBottom: '2px solid rgba(255, 255, 255, 0.3)',
            paddingBottom: '8px',
            marginBottom: '10px',
            marginTop: '15px',
            textTransform: 'uppercase',
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