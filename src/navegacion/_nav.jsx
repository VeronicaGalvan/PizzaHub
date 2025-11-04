import React from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilBell,
  cilCalculator,
  cilChartPie,
  cilDescription,
  cilDrop,
  cilPencil,
  cilPuzzle,
  cilSpeedometer,
  cilStar,
} from '@coreui/icons'
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react'

// Sidebar navigation
const _nav = [
  {
    component: CNavTitle,
    name: 'Analíticas',
    style: { color: '#000000' },
  },
  {
    component: CNavItem,
    name: 'Movimientos',
    to: '/pages/Movimientos',
    icon: <CIcon icon={cilSpeedometer} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },
  {
    component: CNavTitle,
    name: 'PEDIDOS',
    style: { color: '#000000' },
  },
  {
    component: CNavItem,
    name: 'Entrada Pedidos',
    to: '/theme/colors',
    icon: <CIcon icon={cilDrop} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },
  {
    component: CNavItem,
    name: 'En proceso',
    to: '/theme/typography',
    icon: <CIcon icon={cilPencil} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },

  {
    component: CNavTitle,
    name: 'INVENTARIO',
    style: { color: '#000000' },
  },
  {
    component: CNavGroup,
    name: 'Inventario',
    to: '/pages',
    icon: <CIcon icon={cilPuzzle} customClassName="nav-icon" />,
    style: { color: '#000000' },
    items: [
      {
        component: CNavItem,
        name: 'Insumos',
        to: '/pages/Insumos',
        style: { color: '#000000' },
      },
      {
        component: CNavItem,
        name: 'Agregar Insumos',
        to: '/pages/AgregarInsumos',
        style: { color: '#000000' },
      },
      {
        component: CNavItem,
        name: 'Mermas',
        to: '/forms/layout',
        style: { color: '#000000' },
      },
      {
        component: CNavItem,
        name: '...',
        to: '/forms/validation',
        style: { color: '#000000' },
      },
    ],
  },
  {
    component: CNavItem,
    name: 'Provedores',
    to: '/pages/Provedores',
    icon: <CIcon icon={cilChartPie} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },
  {
    component: CNavGroup,
    name: '...',
    icon: <CIcon icon={cilStar} customClassName="nav-icon" />,
    style: { color: '#000000' },
    items: [
      {
        component: CNavItem,
        name: '...',
        to: '/icons/coreui-icons',
        style: { color: '#000000' },
      },
    ],
  },
  {
    component: CNavGroup,
    name: '...',
    icon: <CIcon icon={cilBell} customClassName="nav-icon" />,
    style: { color: '#000000' },
    items: [
      {
        component: CNavItem,
        name: '...',
        to: '/notifications/alerts',
        style: { color: '#000000' },
      },
    ],
  },
  {
    component: CNavItem,
    name: '...',
    to: '/widgets',
    icon: <CIcon icon={cilCalculator} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },
  {
    component: CNavTitle,
    name: 'Usuarios',
    style: { color: '#000000' },
  },
  {
    component: CNavGroup,
    name: 'Usuarios',
    icon: <CIcon icon={cilStar} customClassName="nav-icon" />,
    style: { color: '#000000' },
    items: [
      {
        component: CNavItem,
        name: 'Gestion Usuarios',
        to: '/pages/Usuarios',
        style: { color: '#000000' },
      },
      {
        component: CNavItem,
        name: 'Usuarios Movil',
        to: '/pages/UsuariosMovil',
        style: { color: '#000000' },
      },
    ],
  },
  {
    component: CNavItem,
    name: 'Documentación',
    href: 'https://coreui.io/react/docs/templates/installation/',
    icon: <CIcon icon={cilDescription} customClassName="nav-icon" />,
    style: { color: '#000000' },
  },
]

export default _nav
