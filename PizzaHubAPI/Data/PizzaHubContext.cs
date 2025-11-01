using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Data;

public class PizzaHubContext : DbContext
{
    public PizzaHubContext(DbContextOptions<PizzaHubContext> options) : base(options)
    {
    }

    public DbSet<Usuario> Usuarios { get; set; } = null!;
    public DbSet<Rol> Roles { get; set; } = null!;
    public DbSet<UsuarioRol> UsuariosRoles { get; set; } = null!;
    public DbSet<TokenRevocado> TokensRevocados { get; set; } = null!;
    public DbSet<Producto> Productos { get; set; } = null!;
    public DbSet<MovimientoInventario> MovimientosInventario { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuración de índices y relaciones
        modelBuilder.Entity<Usuario>()
            .HasIndex(u => u.Email)
            .IsUnique();

        modelBuilder.Entity<Rol>()
            .HasIndex(r => r.Nombre)
            .IsUnique();

        modelBuilder.Entity<TokenRevocado>()
            .HasIndex(t => t.Token);

        // Configuración de propiedades de auditoría
        modelBuilder.Entity<Usuario>()
            .Property(u => u.CreadoEn)
            .HasDefaultValueSql("CURRENT_TIMESTAMP(6)");
            
        modelBuilder.Entity<Usuario>()
            .Property(u => u.RowVersion)
            .IsRowVersion();

        // Configuración de Producto
        modelBuilder.Entity<Producto>()
            .Property(p => p.CreadoEn)
            .HasDefaultValueSql("CURRENT_TIMESTAMP(6)");
            
        modelBuilder.Entity<Producto>()
            .Property(p => p.RowVersion)
            .IsRowVersion();

        // Configuración de MovimientoInventario
        modelBuilder.Entity<MovimientoInventario>()
            .Property(m => m.CreadoEn)
            .HasDefaultValueSql("CURRENT_TIMESTAMP(6)");

        modelBuilder.Entity<MovimientoInventario>()
            .HasOne(m => m.Producto)
            .WithMany(p => p.MovimientosInventario)
            .HasForeignKey(m => m.ProductoId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}