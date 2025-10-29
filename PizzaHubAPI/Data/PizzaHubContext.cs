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
            .HasDefaultValueSql("CURRENT_TIMESTAMP");
            
        modelBuilder.Entity<Usuario>()
            .Property(u => u.RowVersion)
            .IsRowVersion();
    }
}