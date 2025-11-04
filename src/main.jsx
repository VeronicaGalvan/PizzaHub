import React from 'react'
import ReactDOM from 'react-dom/client'
import { Provider } from 'react-redux'
import { BrowserRouter } from 'react-router-dom' 
import store from './store'
import App from './App'

ReactDOM.createRoot(document.getElementById('root')).render(
 <React.StrictMode> {/*  Modo pa encontrar errores */}
    <Provider store={store}> {/*  para los useState globales*/} 
      <BrowserRouter>  {/*Para manejar las rutas*/}
        <App />
      </BrowserRouter>
    </Provider>
  </React.StrictMode>
)