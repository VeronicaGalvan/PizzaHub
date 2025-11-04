//import { useState } from 'react'
import AppSidebar from './components/Sidebar';
import './App.css'
import '@coreui/coreui/dist/css/coreui.min.css'
import 'simplebar-react/dist/simplebar.min.css'
import DefaultLayout from './navegacion/DefaultLayout' //  Aqui esta metido el fooder, slider y el principal
import Login from './components/Login';
import { useState } from 'react';
import { HashRouter, Route, Routes } from 'react-router-dom'
     
function App() {


const [inicioSecion, setInicioSecion] = useState(false);




  return (
    <>
      {inicioSecion ? (
        // Si inició sesión, carga el layout principal
        <DefaultLayout />
      ) : (
        // Si no, muestra el login y pasa la función para cambiar el estado
        <Login onSuccess={() => setInicioSecion(true)} />
      )}
    </>
  )
}
export default App