using Microsoft.EntityFrameworkCore;
using PizzaHubAPI.Models;

namespace PizzaHubAPI.Data
{
    public class PizzaHubContext : DbContext
    {
        public PizzaHubContext(DbContextOptions<PizzaHubContext> options)
            : base(options)
        {
        }

        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Rol> Roles { get; set; }
        public DbSet<UsuarioRol> UsuarioRoles { get; set; }
        public DbSet<TokenRevocado> TokensRevocados { get; set; }
        public DbSet<Producto> Productos { get; set; }
        public DbSet<Pedido> Pedidos { get; set; }
        public DbSet<DetallePedido> DetallesPedido { get; set; }
        public DbSet<Repartidor> Repartidores { get; set; }
        public DbSet<MensajeChat> MensajesChat { get; set; }
        public DbSet<Notificacion> Notificaciones { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuración de Usuario-Roles (N:N)
            modelBuilder.Entity<UsuarioRol>()
                .HasKey(ur => new { ur.UsuarioId, ur.RolId });

            modelBuilder.Entity<UsuarioRol>()
                .HasOne(ur => ur.Usuario)
                .WithMany(u => u.UsuarioRoles)
                .HasForeignKey(ur => ur.UsuarioId);

            modelBuilder.Entity<UsuarioRol>()
                .HasOne(ur => ur.Rol)
                .WithMany(r => r.UsuarioRoles)
                .HasForeignKey(ur => ur.RolId);

            // Configuración de Pedido-DetallePedido (1:N)
            modelBuilder.Entity<DetallePedido>()
                .HasOne(d => d.Pedido)
                .WithMany(p => p.DetallesPedido)
                .HasForeignKey(d => d.PedidoId);

            // Configuración de Usuario-Repartidor (1:1)
            modelBuilder.Entity<Repartidor>()
                .HasOne(r => r.Usuario)
                .WithOne(u => u.Repartidor)
                .HasForeignKey<Repartidor>(r => r.UsuarioId);

            // Índices
            modelBuilder.Entity<Usuario>()
                .HasIndex(u => u.Email)
                .IsUnique();

            modelBuilder.Entity<Producto>()
                .HasIndex(p => p.Nombre);

            modelBuilder.Entity<Pedido>()
                .HasIndex(p => p.FechaPedido);

            modelBuilder.Entity<Repartidor>()
                .HasIndex(r => r.Estado);

            // Valores por defecto para Roles
            modelBuilder.Entity<Rol>().HasData(
                new Rol { Id = 1, Nombre = "ADMIN" },
                new Rol { Id = 2, Nombre = "CLIENTE" },
                new Rol { Id = 3, Nombre = "REPARTIDOR" }
            );
        }
    }
}