using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Data;

public class PizzaHubContext : DbContext
{
    public PizzaHubContext(DbContextOptions<PizzaHubContext> options) : base(options)
    {
    }

    // Nuevas tablas según el esquema de base de datos
    public DbSet<Usuario> Usuarios { get; set; } = null!;
    public DbSet<Cliente> Clientes { get; set; } = null!;
    public DbSet<Empleado> Empleados { get; set; } = null!;
    public DbSet<Repartidor> Repartidores { get; set; } = null!;
    public DbSet<Insumo> Insumos { get; set; } = null!;
    public DbSet<Producto> Productos { get; set; } = null!;
    public DbSet<Pedido> Pedidos { get; set; } = null!;
    public DbSet<DetallePedido> DetallesPedido { get; set; } = null!;
    public DbSet<Caja> Cajas { get; set; } = null!;
    public DbSet<Venta> Ventas { get; set; } = null!;
    public DbSet<Calificacion> Calificaciones { get; set; } = null!;
    public DbSet<InventarioLog> InventarioLogs { get; set; } = null!;
    public DbSet<TokenRevocado> TokensRevocados { get; set; } = null!;
    public DbSet<CompraInsumo> ComprasInsumos { get; set; } = null!;
    public DbSet<DetalleCompraInsumo> DetallesCompraInsumos { get; set; } = null!;
    public DbSet<Notificacion> Notificaciones { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuración de Usuario
        modelBuilder.Entity<Usuario>()
            .HasIndex(u => u.Correo)
            .IsUnique();

        // Configuración de TokenRevocado
        modelBuilder.Entity<TokenRevocado>(entity =>
        {
            entity.Property(t => t.TokenHash)
                .HasMaxLength(64)
                .IsRequired();
            
            // Índice en el hash del token para búsquedas rápidas
            entity.HasIndex(t => t.TokenHash);
        });

        // Configuración de Empleado
        modelBuilder.Entity<Empleado>()
            .HasOne(e => e.Usuario)
            .WithMany(u => u.Empleados)
            .HasForeignKey(e => e.UsuarioId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de Cliente
        modelBuilder.Entity<Cliente>()
            .HasOne(c => c.Usuario)
            .WithMany(u => u.Clientes)
            .HasForeignKey(c => c.UsuarioId)
            .OnDelete(DeleteBehavior.SetNull);

        // Configuración de Repartidor
        modelBuilder.Entity<Repartidor>()
            .HasOne(r => r.Usuario)
            .WithMany(u => u.Repartidores)
            .HasForeignKey(r => r.UsuarioId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de Insumo
        modelBuilder.Entity<Insumo>()
            .Property(i => i.UltimaActualizacion)
            .ValueGeneratedOnAddOrUpdate();

        // Configuración de Pedido
        modelBuilder.Entity<Pedido>()
            .HasOne(p => p.Cliente)
            .WithMany(c => c.Pedidos)
            .HasForeignKey(p => p.ClienteId)
            .OnDelete(DeleteBehavior.SetNull);

        modelBuilder.Entity<Pedido>()
            .HasOne(p => p.Repartidor)
            .WithMany(r => r.Pedidos)
            .HasForeignKey(p => p.RepartidorId)
            .OnDelete(DeleteBehavior.SetNull);

        // Configuración de DetallePedido
        modelBuilder.Entity<DetallePedido>()
            .HasOne(d => d.Pedido)
            .WithMany(p => p.Detalles)
            .HasForeignKey(d => d.PedidoId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<DetallePedido>()
            .HasOne(d => d.Producto)
            .WithMany(p => p.DetallesPedido)
            .HasForeignKey(d => d.ProductoId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de Caja
        modelBuilder.Entity<Caja>()
            .HasIndex(c => c.Fecha)
            .IsUnique();

        modelBuilder.Entity<Caja>()
            .HasOne(c => c.Empleado)
            .WithMany(e => e.Cajas)
            .HasForeignKey(c => c.EmpleadoId)
            .OnDelete(DeleteBehavior.SetNull);

        // Configuración de Venta
        modelBuilder.Entity<Venta>()
            .HasOne(v => v.Caja)
            .WithMany(c => c.Ventas)
            .HasForeignKey(v => v.CajaId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Venta>()
            .HasOne(v => v.Pedido)
            .WithMany(p => p.Ventas)
            .HasForeignKey(v => v.PedidoId)
            .OnDelete(DeleteBehavior.SetNull);

        modelBuilder.Entity<Venta>()
            .HasOne(v => v.Empleado)
            .WithMany(e => e.Ventas)
            .HasForeignKey(v => v.EmpleadoId)
            .OnDelete(DeleteBehavior.SetNull);

        // Configuración de Calificacion
        modelBuilder.Entity<Calificacion>()
            .HasOne(c => c.Pedido)
            .WithMany(p => p.Calificaciones)
            .HasForeignKey(c => c.PedidoId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de InventarioLog
        modelBuilder.Entity<InventarioLog>()
            .HasOne(i => i.Insumo)
            .WithMany(ins => ins.InventarioLogs)
            .HasForeignKey(i => i.InsumoId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de CompraInsumo
        modelBuilder.Entity<CompraInsumo>()
            .HasOne(c => c.Empleado)
            .WithMany(e => e.ComprasInsumos)
            .HasForeignKey(c => c.EmpleadoId)
            .OnDelete(DeleteBehavior.SetNull);

        // Configuración de DetalleCompraInsumo
        modelBuilder.Entity<DetalleCompraInsumo>()
            .HasOne(d => d.Compra)
            .WithMany(c => c.Detalles)
            .HasForeignKey(d => d.CompraId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<DetalleCompraInsumo>()
            .HasOne(d => d.Insumo)
            .WithMany()
            .HasForeignKey(d => d.InsumoId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configuración de Notificacion
        modelBuilder.Entity<Notificacion>()
            .HasOne(n => n.Cliente)
            .WithMany(c => c.Notificaciones)
            .HasForeignKey(n => n.ClienteId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Notificacion>()
            .HasOne(n => n.Pedido)
            .WithMany()
            .HasForeignKey(n => n.PedidoId)
            .OnDelete(DeleteBehavior.SetNull);
    }
}