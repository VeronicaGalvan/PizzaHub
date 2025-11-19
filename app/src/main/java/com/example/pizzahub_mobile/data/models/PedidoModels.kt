package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

/** Request para crear un nuevo pedido POST /api/PedidosNew/registrar */
data class PedidoRequest(
        @Json(name = "clienteId") val clienteId: Int,
        @Json(name = "tipo") val tipo: Int, // 1=Local, 2=ParaLlevar, 3=Express, 4=Domicilio
        @Json(name = "metodoPago") val metodoPago: Int, // 1=Efectivo, 2=Tarjeta, etc.
        @Json(name = "origen") val origen: Int = 0, // 0=App, 1=Web, 2=Presencial
        @Json(name = "direccionEntrega") val direccionEntrega: String? = null,
        @Json(name = "observaciones") val observaciones: String? = null,
        @Json(name = "detalles") val detalles: List<PedidoDetalle>
)

data class PedidoDetalle(
        @Json(name = "productoId") val productoId: Int,
        @Json(name = "cantidad") val cantidad: Int
)

/**
 * Response del backend al crear un pedido Coincide con la respuesta real de POST
 * /api/PedidosNew/registrar y GET /api/PedidosNew/{id}
 */
data class PedidoResponse(
        @Json(name = "id") val id: Int,
        @Json(name = "clienteId") val clienteId: Int,
        @Json(name = "clienteNombre") val clienteNombre: String? = null,
        @Json(name = "repartidorId") val repartidorId: Int? = null,
        @Json(name = "repartidorNombre") val repartidorNombre: String? = null,
        @Json(name = "tipo") val tipo: String, // "App", "Presencial", etc.
        @Json(name = "estado") val estado: String, // "Pendiente", "En preparación", etc.
        @Json(name = "metodoPago") val metodoPago: String, // "Efectivo", "Tarjeta", etc.
        @Json(name = "origen") val origen: String, // "App", "Web", etc.
        @Json(name = "total") val total: Double,
        @Json(name = "direccionEntrega") val direccionEntrega: String? = null,
        @Json(name = "observaciones") val observaciones: String? = null,
        @Json(name = "fechaPedido") val fechaPedido: String? = null,
        @Json(name = "detalles") val detalles: List<PedidoDetalleResponse>? = null
)

/** Detalle de producto en la respuesta del pedido */
data class PedidoDetalleResponse(
        @Json(name = "id") val id: Int,
        @Json(name = "productoId") val productoId: Int,
        @Json(name = "productoNombre") val productoNombre: String,
        @Json(name = "cantidad") val cantidad: Int,
        @Json(name = "subtotal") val subtotal: Double
)

/** Tipos de pedido según backend */
enum class TipoPedido(val value: Int) {
    LOCAL(1),
    PARA_LLEVAR(2),
    EXPRESS(3),
    DOMICILIO(4)
}

/** Métodos de pago según backend */
enum class MetodoPago(val value: Int) {
    EFECTIVO(1),
    TARJETA(2),
    TRANSFERENCIA(3)
}

/** Estados de pedido según backend */
enum class EstadoPedido(val value: Int, val descripcion: String) {
    PENDIENTE(1, "Pendiente"),
    EN_PREPARACION(2, "En preparación"),
    EN_CAMINO(3, "En camino"),
    ENTREGADO(4, "Entregado"),
    CANCELADO(5, "Cancelado");

    companion object {
        fun fromValue(value: Int): EstadoPedido? = values().firstOrNull { it.value == value }

        /**
         * Convierte el string del backend a EstadoPedido "Pendiente" -> PENDIENTE, "En preparación"
         * -> EN_PREPARACION, etc.
         */
        fun fromString(estado: String): EstadoPedido? {
            return when (estado.lowercase()) {
                "pendiente" -> PENDIENTE
                "en preparación", "en preparacion", "preparación", "preparacion" -> EN_PREPARACION
                "en camino", "encamino" -> EN_CAMINO
                "entregado" -> ENTREGADO
                "cancelado" -> CANCELADO
                else -> null
            }
        }
    }
}
