import React from "react";
// ../components/index.js
import AppSidebar  from "../components/Sidebar";
//import { AppSidebar } from "../components/Sidebar";
import Login from "../components/Login";
import AppContent from "../components/appContent";
const DefaultLayout = () =>{
    return(
        <div>  
            <AppSidebar/>  
            <div className="wrapper d-flex flex-column min-vh-100">
                <div className="body flex-grow-1">
                <AppContent/>




                </div>
                </div>


                <div className="body flex-grow-1">
                </div>

<h1>Footer</h1>
            </div>
    )
}
export default DefaultLayout