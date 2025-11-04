-- ======================================================
-- UNIVERSIDAD TECNOLÓGICA DE LEÓN
-- CARRERA: INGENIERÍA EN DESARROLLO Y GESTIÓN DE SOFTWARE
-- MATERIA: DESARROLLO MÓVIL INTEGRAL
-- PROYECTO FINAL: SISTEMA INTEGRAL PIZZAHUB
-- AUTORES:
--   - Galván García Verónica Lizethe
--   - Jasso Flores Miguel Ernesto
--   - Macías Estrada Ulises
--   - Jose Antonio Gomez Valades 		
-- GRUPO: IDGS1002
-- FECHA: 03/Nov/2025
-- ======================================================
-- DESCRIPCIÓN:
-- Base de datos central para el sistema PizzaHub, que integra:
--   • PWA administrativa (inventario, ventas, usuarios, reportes)
--   • App móvil Android (pedidos, chat, seguimiento, calificación)
-- Esta base de datos soporta la API compartida entre ambas plataformas,
-- garantizando integridad, trazabilidad y sincronización en tiempo real.
-- ======================================================

CREATE DATABASE IF NOT EXISTS pizzahub CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE pizzahub;

-- ======================================================
-- 1️ USUARIOS Y PERSONAS
-- ======================================================

-- Tabla principal de usuarios del sistema (autenticación y roles)
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,              -- Email para login
    contraseña_hash VARCHAR(255) NOT NULL,           -- Contraseña en hash seguro (bcrypt o similar)
    rol ENUM('Administrador','Empleado','Repartidor','Cliente') NOT NULL, -- Rol del sistema
    estado ENUM('Activo','Inactivo') DEFAULT 'Activo', -- Control lógico de usuarios
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP -- Fecha y hora de alta
);

-- Información personal asociada (aplica para todos los tipos de usuario)
CREATE TABLE personas (
    id_persona INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL,                             -- Relación con usuario (puede ser nula)
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50),
    telefono VARCHAR(15) UNIQUE NOT NULL,
    colonia VARCHAR(100),
    calle VARCHAR(100),
    numero VARCHAR(10),
    municipio VARCHAR(100),
    estado VARCHAR(100),
    codigo_postal VARCHAR(10),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- ======================================================
-- 2️ EMPLEADOS / REPARTIDORES / CLIENTES
-- ======================================================

-- Tabla de empleados administrativos o de cocina
CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    id_persona INT NOT NULL,
    sobrenombre VARCHAR(50),
    horario_trabajo VARCHAR(100),
    estado ENUM('Activo','Inactivo') DEFAULT 'Activo',
    FOREIGN KEY (id_persona) REFERENCES personas(id_persona) ON DELETE CASCADE
);

-- Tabla de repartidores (asignación de entregas y control operativo)
CREATE TABLE repartidores (
    id_repartidor INT AUTO_INCREMENT PRIMARY KEY,
    id_persona INT NOT NULL,
    sobrenombre VARCHAR(50),
    vehiculo_asignado VARCHAR(100),
    horario_trabajo VARCHAR(100),
    estado ENUM('Activo','Inactivo') DEFAULT 'Activo',
    estado_actual ENUM('Disponible','En entrega','Desconectado') DEFAULT 'Desconectado',
    FOREIGN KEY (id_persona) REFERENCES personas(id_persona) ON DELETE CASCADE
);

-- Tabla de clientes (para App móvil)
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_persona INT NOT NULL,
    comentarios_domicilio VARCHAR(255),
    distancia_estimacion DECIMAL(6,2),               -- En km, estimación para entregas
    FOREIGN KEY (id_persona) REFERENCES personas(id_persona) ON DELETE CASCADE
);

-- ======================================================
-- 3️ INVENTARIO Y MATERIA PRIMA
-- ======================================================

-- Proveedores de materia prima
CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    direccion VARCHAR(255)
);

-- Materias primas usadas para la elaboración de productos
CREATE TABLE materia_prima (
    id_materia INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad_actual DECIMAL(10,2) DEFAULT 0,
    unidad_medida ENUM('Kg','Gr','Litros','Unidad') NOT NULL,
    id_proveedor INT,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    punto_reorden DECIMAL(10,2) DEFAULT 0,           -- Umbral mínimo para alertar reposición
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor) ON DELETE SET NULL
);

-- ======================================================
-- 4️ PRODUCTOS
-- ======================================================

-- Catálogo general de productos (pizzas, bebidas, complementos)
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    imagen VARCHAR(255),                             -- URL o ruta del recurso
    disponible BOOLEAN DEFAULT TRUE,
    categoria ENUM('Pizza','Bebida','Complemento') NOT NULL
);

-- ======================================================
-- 5️ PEDIDOS Y DETALLES
-- ======================================================

-- Registro maestro de pedidos realizados (por App o PWA)
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NULL,                             -- Cliente que realiza el pedido (puede ser externo)
    plataforma_origen ENUM('App Móvil','PWA','Mostrador','Uber','Didi') DEFAULT 'App Móvil',
    estado ENUM('En preparación','En camino','Entregado','Cancelado') DEFAULT 'En preparación',
    tipo_entrega ENUM('Recoger','Domicilio') DEFAULT 'Domicilio',
    id_repartidor INT NULL,                          -- Repartidor asignado (si aplica)
    metodo_pago ENUM('Efectivo','Tarjeta','App Externa','Transferencia','Venta sin remuneración') DEFAULT 'Efectivo',
    total DECIMAL(10,2) DEFAULT 0,
    id_sesion_caja INT NULL,                         -- Turno de caja asociado
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega DATETIME NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE SET NULL,
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id_repartidor) ON DELETE SET NULL
);

-- Relación de productos vendidos dentro de un pedido
CREATE TABLE pedido_detalles (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- Historial de cambios de estado para auditoría y seguimiento en tiempo real
CREATE TABLE pedido_estados_historial (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    estado_anterior ENUM('En preparación','En camino','Entregado','Cancelado'),
    estado_nuevo ENUM('En preparación','En camino','Entregado','Cancelado'),
    fecha_cambio DATETIME DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT NULL,                              -- Usuario que realizó el cambio
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- ======================================================
-- 6️ CALIFICACIONES Y CHAT (Nuevas funciones móviles)
-- ======================================================

-- Valoración del servicio y productos por parte del cliente
CREATE TABLE calificaciones_pedidos (
    id_calificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_cliente INT NOT NULL,
    puntuacion INT CHECK (puntuacion BETWEEN 1 AND 5),
    comentario VARCHAR(255),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE
);

-- Mensajes del chat interactivo cliente-sistema (IA básica o predefinida)
CREATE TABLE chat_mensajes (
    id_mensaje INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    mensaje_cliente TEXT,
    respuesta_sistema TEXT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE
);

-- ======================================================
-- 7️ MERMAS
-- ======================================================

-- Registro de pérdidas de materia prima por causas diversas
CREATE TABLE mermas (
    id_merma INT AUTO_INCREMENT PRIMARY KEY,
    id_materia INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    tipo_merma ENUM('Quemado','Mal manejo','Caducidad','Rotura','Evaporación','Corte') NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    comentarios VARCHAR(255),
    FOREIGN KEY (id_materia) REFERENCES materia_prima(id_materia)
);

-- ======================================================
-- 8️ CAJA Y MOVIMIENTOS
-- ======================================================

-- Control de turnos de caja (apertura y cierre por usuario)
CREATE TABLE sesiones_caja (
    id_sesion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_apertura INT NOT NULL,                 -- Quién abrió la caja
    id_usuario_cierre INT NULL,                       -- Quién la cerró
    fecha_apertura DATETIME DEFAULT CURRENT_TIMESTAMP,
    fondo_inicial DECIMAL(10,2) DEFAULT 0,
    fecha_cierre DATETIME NULL,
    efectivo_final DECIMAL(10,2) DEFAULT 0,
    ventas_sistema DECIMAL(10,2) DEFAULT 0,
    diferencia DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (id_usuario_apertura) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_usuario_cierre) REFERENCES usuarios(id_usuario)
);

-- Asociación directa entre pedido y turno de caja (añadida para control contable)
ALTER TABLE pedidos
ADD CONSTRAINT fk_pedido_sesion
FOREIGN KEY (id_sesion_caja) REFERENCES sesiones_caja(id_sesion)
ON DELETE SET NULL;

-- Movimientos financieros dentro de una sesión de caja
CREATE TABLE movimientos_caja (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_sesion INT NOT NULL,
    tipo_movimiento ENUM('Entrada','Salida') NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    concepto VARCHAR(255),
    id_usuario INT NOT NULL,                          -- Quién registró el movimiento
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_sesion) REFERENCES sesiones_caja(id_sesion) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ======================================================
-- 9️ ÍNDICES Y OPTIMIZACIÓN
-- ======================================================

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_personas_telefono ON personas(telefono);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha_registro);
CREATE INDEX idx_materia_nombre ON materia_prima(nombre);
CREATE INDEX idx_productos_categoria ON productos(categoria);

-- ======================================================
-- 🔚 FIN DEL SCRIPT - BASE DE DATOS PIZZAHUB V1.1
-- ======================================================
-- NOTAS:
-- • Base de datos optimizada y modular para uso académico y profesional.
-- • Compatible con API REST en .NET y clientes front-end en React / Kotlin.
-- • Cumple con integridad referencial y diseño en 3FN.
-- ======================================================
