using System.ComponentModel.DataAnnotations;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Models.DTOs;

// DTO para registrar un pedido con su detalle
public class RegistrarPedidoDto
{
    public int? ClienteId { get; set; }
    
    [Required]
    public TipoPedidoEnum Tipo { get; set; }
    
    [Required]
    public MetodoPagoEnum MetodoPago { get; set; }
    
    [Required]
    public OrigenPedidoEnum Origen { get; set; }
    
    public string? DireccionEntrega { get; set; }
    
    public string? Observaciones { get; set; }
    
    [Required]
    public List<DetallePedidoDto> Detalles { get; set; } = new List<DetallePedidoDto>();
}

public class DetallePedidoDto
{
    [Required]
    public int ProductoId { get; set; }
    
    [Required]
    [Range(1, int.MaxValue)]
    public int Cantidad { get; set; }
}

// DTO para asignar repartidor a un pedido
public class AsignarRepartidorDto
{
    [Required]
    public int RepartidorId { get; set; }
}

// DTO para registrar una venta vinculada a caja
public class RegistrarVentaDto
{
    [Required]
    public int CajaId { get; set; }
    
    public int? PedidoId { get; set; }
    
    public int? EmpleadoId { get; set; }
    
    [Required]
    public MetodoPagoEnum MetodoPago { get; set; }
    
    [Required]
    [Range(0.01, double.MaxValue)]
    public decimal Total { get; set; }
}

// DTO para cerrar caja con resumen
public class CerrarCajaDto
{
    [Required]
    [Range(0, double.MaxValue)]
    public decimal SaldoFinal { get; set; }
}

// DTO para respuesta de resumen de caja
public class ResumenCajaDto
{
    public int Id { get; set; }
    public DateTime Fecha { get; set; }
    public decimal SaldoInicial { get; set; }
    public decimal SaldoFinal { get; set; }
    public decimal TotalVentas { get; set; }
    public int CantidadVentas { get; set; }
    public Dictionary<string, decimal> VentasPorMetodoPago { get; set; } = new Dictionary<string, decimal>();
    public string? EmpleadoNombre { get; set; }
}

// DTO para registrar calificación
public class RegistrarCalificacionDto
{
    [Required]
    [Range(1, 5, ErrorMessage = "Las estrellas deben estar entre 1 y 5")]
    public int Estrellas { get; set; }
    
    public string? Comentario { get; set; }
}

// DTO para crear empleado
public class CrearEmpleadoDto
{
    [Required]
    [MaxLength(50)]
    public string Nombre { get; set; } = null!;
    
    [Required]
    [MaxLength(50)]
    public string Apellidos { get; set; } = null!;
    
    [MaxLength(20)]
    public string? Telefono { get; set; }
    
    [Required]
    public int UsuarioId { get; set; }
}

// DTO para crear insumo (con stock inicial opcional)
public class CrearInsumoDto
{
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = null!;
    
    [Required]
    public UnidadMedidaEnum UnidadMedida { get; set; }
    
    [Range(0, double.MaxValue)]
    public decimal StockInicial { get; set; } = 0;
    
    [Range(0, double.MaxValue)]
    public decimal StockMinimo { get; set; } = 0;
}

// DTO para registrar una compra de insumos (múltiples insumos)
public class RegistrarCompraInsumosDto
{
    [Required]
    [MaxLength(100)]
    public string Proveedor { get; set; } = null!;
    
    [MaxLength(50)]
    public string? NumeroFactura { get; set; }
    
    public string? Observaciones { get; set; }
    
    [Required]
    public List<DetalleCompraInsumoDto> Detalles { get; set; } = new List<DetalleCompraInsumoDto>();
}

public class DetalleCompraInsumoDto
{
    [Required]
    public int InsumoId { get; set; }
    
    [Required]
    [Range(0.01, double.MaxValue)]
    public decimal Cantidad { get; set; }
    
    [Required]
    [Range(0.01, double.MaxValue)]
    public decimal PrecioUnitario { get; set; }
}

// DTO para registrar movimiento de inventario
public class RegistrarMovimientoInventarioDto
{
    [Required]
    public int InsumoId { get; set; }
    
    [Required]
    [Range(0.01, double.MaxValue)]
    public decimal Cantidad { get; set; }
    
    [Required]
    public TipoMovimientoEnum TipoMovimiento { get; set; }
    
    public string? Motivo { get; set; }
}

// DTO para abrir caja
public class AbrirCajaDto
{
    [Required]
    [Range(0, double.MaxValue)]
    public decimal SaldoInicial { get; set; }
    
    public int? EmpleadoId { get; set; }
}

// DTO respuesta de pedido completo
public class PedidoCompletoDto
{
    public int Id { get; set; }
    public int? ClienteId { get; set; }
    public string? ClienteNombre { get; set; }
    public int? RepartidorId { get; set; }
    public string? RepartidorNombre { get; set; }
    public string Tipo { get; set; } = null!;
    public string Estado { get; set; } = null!;
    public string MetodoPago { get; set; } = null!;
    public string Origen { get; set; } = null!;
    public decimal Total { get; set; }
    public string? DireccionEntrega { get; set; }
    public string? Observaciones { get; set; }
    public DateTime FechaPedido { get; set; }
    public List<DetallePedidoCompletoDto> Detalles { get; set; } = new List<DetallePedidoCompletoDto>();
}

public class DetallePedidoCompletoDto
{
    public int Id { get; set; }
    public int ProductoId { get; set; }
    public string ProductoNombre { get; set; } = null!;
    public int Cantidad { get; set; }
    public decimal Subtotal { get; set; }
}
