import React from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilChartLine,
  cilCart,
  cilClock,
  cilLibrary,
  cilBasket,
  cilPlus,
  cilWarning,
  cilTruck,
  cilBell,
  cilPeople,
  cilUserPlus,
  cilMobile,
  cilBook,
  cilMoney,
} from '@coreui/icons'
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react'

const _nav = [
  {
    component: CNavTitle,
    name: 'Analíticas',
  },
  {
    component: CNavItem,
    name: 'Movimientos',
    to: '/pages/Movimientos',
    icon: <CIcon icon={cilChartLine} customClassName="nav-icon" />,
  },
  {
    component: CNavTitle,
    name: 'Pedidos',
  },
  {
    component: CNavItem,
    name: 'Entrada Pedidos',
    to: '/pages/EntradaPedidos',
    icon: <CIcon icon={cilCart} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'En proceso',
    to: '/pages/Enproceso',
    icon: <CIcon icon={cilClock} customClassName="nav-icon" />,
  },

  {
    component: CNavTitle,
    name: 'Inventario',
  },
  {
    component: CNavGroup,
    name: 'Inventario',
    to: '/pages',
    icon: <CIcon icon={cilLibrary} customClassName="nav-icon" />,
    items: [
      {
        component: CNavItem,
        name: 'Insumos',
        to: '/pages/Insumos',
        icon: <CIcon icon={cilLibrary} customClassName="nav-icon" />,
      },
      {
        component: CNavItem,
        name: 'Compra Insumos',
        to: '/pages/CompraInsumos',
        icon: <CIcon icon={cilBasket} customClassName="nav-icon" />,
      },
      {
        component: CNavItem,
        name: 'Agregar Insumos',
        to: '/pages/AgregarInsumos',
        icon: <CIcon icon={cilPlus} customClassName="nav-icon" />,
      },
    ],
  },
 
  {
    component: CNavGroup,
    name: 'Repartidores',
    icon: <CIcon icon={cilTruck} customClassName="nav-icon" />,
    items: [
      {
        component: CNavItem,
        name: 'Repartidores',
        to: '/pages/Repartidores',
        icon: <CIcon icon={cilTruck} customClassName="nav-icon" />,
      },
    ],
  },
  {
    component: CNavItem,
    name: 'Notificaciones',
    to: 'pages/notificaciones',
    icon: <CIcon icon={cilBell} customClassName="nav-icon" />,
  },
  {
    component: CNavTitle,
    name: 'Usuarios',
  },
  {
    component: CNavGroup,
    name: 'Usuarios',
    icon: <CIcon icon={cilPeople} customClassName="nav-icon" />,
    items: [
      {
        component: CNavItem,
        name: 'Gestión Usuarios',
        to: '/pages/Usuarios',
        icon: <CIcon icon={cilPeople} customClassName="nav-icon" />,
      },
      {
        component: CNavItem,
        name: 'Registro de empleados',
        to: '/pages/RegistroEmpleados',
        icon: <CIcon icon={cilUserPlus} customClassName="nav-icon" />,
      },
      {
        component: CNavItem,
        name: 'Clientes',
        to: '/pages/UsuariosMovil',
        icon: <CIcon icon={cilMobile} customClassName="nav-icon" />,
      },
    ],
  },
  {
    component: CNavItem,
    name: 'Documentación',
    href: 'https://coreui.io/react/docs/templates/installation/',
    icon: <CIcon icon={cilBook} customClassName="nav-icon" />,
  },
  {
    component: CNavTitle,
    name: 'Caja',
  },
  {
    component: CNavItem,
    name: 'Abrir/Cerrar Caja',
    to: '/pages/Caja',
    icon: <CIcon icon={cilMoney} customClassName="nav-icon" />,
  },
]

export default _nav