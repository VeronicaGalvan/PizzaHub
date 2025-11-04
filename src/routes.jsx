import React from "react";

const Insumos = React.lazy(() => import('./pages/Insumos'))
const AgregarInsumos = React.lazy(() => import('./pages/AgregarInsumos'))
const Provedores = React.lazy(() => import('./pages/Provedores'))
const Usuarios = React.lazy(() => import('./pages/Usuarios'))
const UsuariosMovil = React.lazy(() => import('./pages/UsuariosMovil'))
const Movimientos = React.lazy(() => import('./pages/Movimientos'))

const routes = [

    { path: '/', exact: true, name: 'home' },
    { path: '/pages/Insumos', name: 'Insumos', element: Insumos },
    { path: '/pages/AgregarInsumos', name: 'AgregarInsumos', element: AgregarInsumos },
    { path: '/pages/Provedores', name: 'Provedores', element: Provedores },
    { path: '/pages/Usuarios', name: 'Usuarios', element: Usuarios },
    { path: '/pages/UsuariosMovil', name: 'UsuariosMovil', element: UsuariosMovil },
    { path: '/pages/Movimientos', name: 'Movimientos', element: Movimientos },

]
export default routes