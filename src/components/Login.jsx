import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import {
  CButton,
  CCard,
  CCardBody,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CInputGroup,
  CInputGroupText,
  CRow,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { cilLockLocked, cilUser } from "@coreui/icons";
import AuthContext from "../context/AuthContext";

const Login = () => {
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const [form, setForm] = useState({
    usuario: "",
    contraseña: "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("https://localhost:7188/api/v1/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: form.usuario,
          password: form.contraseña
        }),
      });

      if (!response.ok) {
        alert("Credenciales incorrectas");
        return;
      }

      const data = await response.json();
      login(data.accessToken, data.usuario, data.roles);
      navigate("/dashboard");

    } catch (error) {
      console.error(error);
      alert("Error al conectar con el servidor");
    }
  };

  return (
    <div
      className="min-vh-100 d-flex flex-row align-items-center"
      style={{
        background: "linear-gradient(135deg, #ff8c00 0%, #ff6b00 50%, #ff4500 100%)",
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Elementos decorativos de fondo */}
      <div
        style={{
          position: "absolute",
          top: "-10%",
          right: "-5%",
          width: "400px",
          height: "400px",
          borderRadius: "50%",
          background: "rgba(255, 255, 255, 0.1)",
          filter: "blur(60px)",
        }}
      />
      <div
        style={{
          position: "absolute",
          bottom: "-10%",
          left: "-5%",
          width: "500px",
          height: "500px",
          borderRadius: "50%",
          background: "rgba(255, 255, 255, 0.08)",
          filter: "blur(80px)",
        }}
      />

      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={8} lg={6} xl={5}>
            <CCard
              className="shadow-lg"
              style={{
                borderRadius: "20px",
                border: "none",
                backdropFilter: "blur(10px)",
                backgroundColor: "rgba(255, 255, 255, 0.95)",
              }}
            >
              <CCardBody className="p-5">
                <div className="text-center mb-4">
                  <div
                    style={{
                      width: "120px",
                      height: "120px",
                      margin: "0 auto 20px",
                    }}
                  >
                    <img
                      src="/logoPizza.jpg"
                      alt="Logo PizzaHub"
                      style={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                        borderRadius: "50%",
                        border: "4px solid #ff8c00",
                        boxShadow: "0 8px 20px rgba(255, 140, 0, 0.3)",
                      }}
                    />
                  </div>
                  <h2 style={{ color: "#333", fontWeight: "bold" }}>
                    Bienvenido
                  </h2>
                  <p style={{ color: "#666", marginTop: "10px" }}>
                    Inicia sesión para continuar
                  </p>
                </div>

                <CForm onSubmit={handleLogin}>
                  <CInputGroup className="mb-3">
                    <CInputGroupText
                      style={{
                        backgroundColor: "#fff",
                        borderColor: "#ddd",
                      }}
                    >
                      <CIcon icon={cilUser} style={{ color: "#ff8c00" }} />
                    </CInputGroupText>
                    <CFormInput
                      name="usuario"
                      placeholder="Usuario"
                      autoComplete="username"
                      value={form.usuario}
                      onChange={handleChange}
                      style={{
                        borderColor: "#ddd",
                      }}
                    />
                  </CInputGroup>

                  <CInputGroup className="mb-4">
                    <CInputGroupText
                      style={{
                        backgroundColor: "#fff",
                        borderColor: "#ddd",
                      }}
                    >
                      <CIcon icon={cilLockLocked} style={{ color: "#ff8c00" }} />
                    </CInputGroupText>
                    <CFormInput
                      type="password"
                      name="contraseña"
                      placeholder="Contraseña"
                      autoComplete="current-password"
                      value={form.contraseña}
                      onChange={handleChange}
                      style={{
                        borderColor: "#ddd",
                      }}
                    />
                  </CInputGroup>

                  <CButton
                    type="submit"
                    style={{
                      backgroundColor: "#ff8c00",
                      borderColor: "#ff8c00",
                      width: "100%",
                      padding: "12px",
                      fontSize: "16px",
                      fontWeight: "600",
                      borderRadius: "10px",
                      transition: "all 0.3s ease",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#ff7a00";
                      e.target.style.transform = "translateY(-2px)";
                      e.target.style.boxShadow = "0 6px 20px rgba(255, 140, 0, 0.4)";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "#ff8c00";
                      e.target.style.transform = "translateY(0)";
                      e.target.style.boxShadow = "none";
                    }}
                  >
                    Iniciar Sesión
                  </CButton>
                </CForm>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default Login;