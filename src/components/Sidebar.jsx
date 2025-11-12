import React from 'react'
import { useSelector, useDispatch } from 'react-redux' // biblioteca para manejar el estado global

import {
  CCloseButton,
  CSidebar,
  CSidebarBrand,
  CSidebarFooter,
  CSidebarHeader,
  CSidebarToggler,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

import { AppSidebarNav } from './SidebarNav'


// sidebar nav config
import navigation from '../navegacion/_nav'

const AppSidebar = () => {
  const dispatch = useDispatch()
  const unfoldable = useSelector((state) => state.sidebarUnfoldable)
  const sidebarShow = useSelector((state) => state.sidebarShow)

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
  background: 'linear-gradient(45deg, #ffb984 0%, #fe7947 100%)', // Degradado como un string
  opacity: 1,
  padding: '10px 0',
}}
    >
      <CSidebarHeader className="border-bottom d-flex align-items-center justify-content-center"
  style={{
    backgroundColor: '#fa4802e7',
    padding: '10px 0',
  }}
>

  <h3
    style={{
      color: 'black',
      fontWeight: 'bold',
      letterSpacing: '1px',
      fontSize: '1.5rem',
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
      margin: 0,
    }}
  >
    🍕 PIZZAHUB
  </h3>
        <CSidebarBrand to="/">
      
        </CSidebarBrand>
        <CCloseButton
          className="d-lg-none"
          dark
          onClick={() => dispatch({ type: 'set', sidebarShow: false })}
        />
      </CSidebarHeader>
      <AppSidebarNav items={navigation} />
      <CSidebarFooter className="border-top d-none d-lg-flex">
        <CSidebarToggler
          onClick={() => dispatch({ type: 'set', sidebarUnfoldable: !unfoldable })}
        />
      </CSidebarFooter>
    </CSidebar>
  )
}

export default React.memo(AppSidebar)
