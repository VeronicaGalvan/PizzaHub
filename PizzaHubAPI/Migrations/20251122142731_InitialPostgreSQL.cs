using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace PizzaHubAPI.Migrations
{
    /// <inheritdoc />
    public partial class InitialPostgreSQL : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "insumos",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    unidad_medida = table.Column<string>(type: "character varying(10)", maxLength: 10, nullable: false),
                    stock_actual = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    stock_minimo = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    ultima_actualizacion = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_insumos", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "productos",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    descripcion = table.Column<string>(type: "text", nullable: true),
                    tipo = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: true),
                    precio = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    almacenable = table.Column<bool>(type: "boolean", nullable: false),
                    imagen_url = table.Column<string>(type: "character varying(255)", maxLength: 255, nullable: true),
                    activo = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_productos", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "usuarios",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre_usuario = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    telefono = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),
                    correo = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    password_hash = table.Column<string>(type: "character varying(255)", maxLength: 255, nullable: false),
                    rol = table.Column<int>(type: "integer", nullable: false),
                    activo = table.Column<bool>(type: "boolean", nullable: false),
                    fecha_creacion = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_usuarios", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "inventario_log",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    insumo_id = table.Column<int>(type: "integer", nullable: false),
                    cantidad = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    tipo_movimiento = table.Column<int>(type: "integer", nullable: false),
                    motivo = table.Column<string>(type: "text", nullable: true),
                    fecha = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_inventario_log", x => x.id);
                    table.ForeignKey(
                        name: "FK_inventario_log_insumos_insumo_id",
                        column: x => x.insumo_id,
                        principalTable: "insumos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "clientes",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    apellidos = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    telefono = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),
                    colonia = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: true),
                    calle = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: true),
                    numero_casa = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),
                    observaciones = table.Column<string>(type: "text", nullable: true),
                    usuario_id = table.Column<int>(type: "integer", nullable: true),
                    fcm_token = table.Column<string>(type: "character varying(255)", maxLength: 255, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_clientes", x => x.id);
                    table.ForeignKey(
                        name: "FK_clientes_usuarios_usuario_id",
                        column: x => x.usuario_id,
                        principalTable: "usuarios",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateTable(
                name: "empleados",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    apellidos = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    telefono = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),
                    usuario_id = table.Column<int>(type: "integer", nullable: false),
                    fecha_ingreso = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    activo = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_empleados", x => x.id);
                    table.ForeignKey(
                        name: "FK_empleados_usuarios_usuario_id",
                        column: x => x.usuario_id,
                        principalTable: "usuarios",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "repartidores",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    nombre = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    apellidos = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    telefono = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: true),
                    usuario_id = table.Column<int>(type: "integer", nullable: false),
                    estado = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_repartidores", x => x.id);
                    table.ForeignKey(
                        name: "FK_repartidores_usuarios_usuario_id",
                        column: x => x.usuario_id,
                        principalTable: "usuarios",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TokensRevocados",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    TokenHash = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    FechaRevocacion = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    UsuarioId = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TokensRevocados", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TokensRevocados_usuarios_UsuarioId",
                        column: x => x.UsuarioId,
                        principalTable: "usuarios",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "caja",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    fecha = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    saldo_inicial = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    saldo_final = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    estado = table.Column<int>(type: "integer", nullable: false),
                    empleado_id = table.Column<int>(type: "integer", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_caja", x => x.id);
                    table.ForeignKey(
                        name: "FK_caja_empleados_empleado_id",
                        column: x => x.empleado_id,
                        principalTable: "empleados",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateTable(
                name: "compras_insumos",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    proveedor = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    numero_factura = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: true),
                    total = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    fecha_compra = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    observaciones = table.Column<string>(type: "text", nullable: true),
                    empleado_id = table.Column<int>(type: "integer", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_compras_insumos", x => x.id);
                    table.ForeignKey(
                        name: "FK_compras_insumos_empleados_empleado_id",
                        column: x => x.empleado_id,
                        principalTable: "empleados",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateTable(
                name: "pedidos",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    cliente_id = table.Column<int>(type: "integer", nullable: true),
                    repartidor_id = table.Column<int>(type: "integer", nullable: true),
                    tipo = table.Column<int>(type: "integer", nullable: false),
                    estado = table.Column<int>(type: "integer", nullable: false),
                    metodo_pago = table.Column<int>(type: "integer", nullable: false),
                    origen = table.Column<int>(type: "integer", nullable: false),
                    total = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    direccion_entrega = table.Column<string>(type: "text", nullable: true),
                    observaciones = table.Column<string>(type: "text", nullable: true),
                    fecha_pedido = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_pedidos", x => x.id);
                    table.ForeignKey(
                        name: "FK_pedidos_clientes_cliente_id",
                        column: x => x.cliente_id,
                        principalTable: "clientes",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                    table.ForeignKey(
                        name: "FK_pedidos_repartidores_repartidor_id",
                        column: x => x.repartidor_id,
                        principalTable: "repartidores",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateTable(
                name: "detalle_compra_insumos",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    compra_id = table.Column<int>(type: "integer", nullable: false),
                    insumo_id = table.Column<int>(type: "integer", nullable: false),
                    cantidad = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    precio_unitario = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    subtotal = table.Column<decimal>(type: "numeric(10,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_detalle_compra_insumos", x => x.id);
                    table.ForeignKey(
                        name: "FK_detalle_compra_insumos_compras_insumos_compra_id",
                        column: x => x.compra_id,
                        principalTable: "compras_insumos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_detalle_compra_insumos_insumos_insumo_id",
                        column: x => x.insumo_id,
                        principalTable: "insumos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "calificaciones",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    pedido_id = table.Column<int>(type: "integer", nullable: false),
                    estrellas = table.Column<int>(type: "integer", nullable: false),
                    comentario = table.Column<string>(type: "text", nullable: true),
                    fecha = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_calificaciones", x => x.id);
                    table.ForeignKey(
                        name: "FK_calificaciones_pedidos_pedido_id",
                        column: x => x.pedido_id,
                        principalTable: "pedidos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "detalle_pedido",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    pedido_id = table.Column<int>(type: "integer", nullable: false),
                    producto_id = table.Column<int>(type: "integer", nullable: false),
                    cantidad = table.Column<int>(type: "integer", nullable: false),
                    subtotal = table.Column<decimal>(type: "numeric(10,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_detalle_pedido", x => x.id);
                    table.ForeignKey(
                        name: "FK_detalle_pedido_pedidos_pedido_id",
                        column: x => x.pedido_id,
                        principalTable: "pedidos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_detalle_pedido_productos_producto_id",
                        column: x => x.producto_id,
                        principalTable: "productos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "notificaciones",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    cliente_id = table.Column<int>(type: "integer", nullable: false),
                    titulo = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    mensaje = table.Column<string>(type: "text", nullable: false),
                    tipo = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    pedido_id = table.Column<int>(type: "integer", nullable: true),
                    leida = table.Column<bool>(type: "boolean", nullable: false),
                    enviada = table.Column<bool>(type: "boolean", nullable: false),
                    fecha_creacion = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    fecha_lectura = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_notificaciones", x => x.id);
                    table.ForeignKey(
                        name: "FK_notificaciones_clientes_cliente_id",
                        column: x => x.cliente_id,
                        principalTable: "clientes",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_notificaciones_pedidos_pedido_id",
                        column: x => x.pedido_id,
                        principalTable: "pedidos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateTable(
                name: "ventas",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    caja_id = table.Column<int>(type: "integer", nullable: false),
                    pedido_id = table.Column<int>(type: "integer", nullable: true),
                    empleado_id = table.Column<int>(type: "integer", nullable: true),
                    metodo_pago = table.Column<int>(type: "integer", nullable: false),
                    total = table.Column<decimal>(type: "numeric(10,2)", nullable: false),
                    fecha_venta = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ventas", x => x.id);
                    table.ForeignKey(
                        name: "FK_ventas_caja_caja_id",
                        column: x => x.caja_id,
                        principalTable: "caja",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ventas_empleados_empleado_id",
                        column: x => x.empleado_id,
                        principalTable: "empleados",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                    table.ForeignKey(
                        name: "FK_ventas_pedidos_pedido_id",
                        column: x => x.pedido_id,
                        principalTable: "pedidos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.SetNull);
                });

            migrationBuilder.CreateIndex(
                name: "IX_caja_empleado_id",
                table: "caja",
                column: "empleado_id");

            migrationBuilder.CreateIndex(
                name: "IX_caja_fecha",
                table: "caja",
                column: "fecha",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_calificaciones_pedido_id",
                table: "calificaciones",
                column: "pedido_id");

            migrationBuilder.CreateIndex(
                name: "IX_clientes_usuario_id",
                table: "clientes",
                column: "usuario_id");

            migrationBuilder.CreateIndex(
                name: "IX_compras_insumos_empleado_id",
                table: "compras_insumos",
                column: "empleado_id");

            migrationBuilder.CreateIndex(
                name: "IX_detalle_compra_insumos_compra_id",
                table: "detalle_compra_insumos",
                column: "compra_id");

            migrationBuilder.CreateIndex(
                name: "IX_detalle_compra_insumos_insumo_id",
                table: "detalle_compra_insumos",
                column: "insumo_id");

            migrationBuilder.CreateIndex(
                name: "IX_detalle_pedido_pedido_id",
                table: "detalle_pedido",
                column: "pedido_id");

            migrationBuilder.CreateIndex(
                name: "IX_detalle_pedido_producto_id",
                table: "detalle_pedido",
                column: "producto_id");

            migrationBuilder.CreateIndex(
                name: "IX_empleados_usuario_id",
                table: "empleados",
                column: "usuario_id");

            migrationBuilder.CreateIndex(
                name: "IX_inventario_log_insumo_id",
                table: "inventario_log",
                column: "insumo_id");

            migrationBuilder.CreateIndex(
                name: "IX_notificaciones_cliente_id",
                table: "notificaciones",
                column: "cliente_id");

            migrationBuilder.CreateIndex(
                name: "IX_notificaciones_pedido_id",
                table: "notificaciones",
                column: "pedido_id");

            migrationBuilder.CreateIndex(
                name: "IX_pedidos_cliente_id",
                table: "pedidos",
                column: "cliente_id");

            migrationBuilder.CreateIndex(
                name: "IX_pedidos_repartidor_id",
                table: "pedidos",
                column: "repartidor_id");

            migrationBuilder.CreateIndex(
                name: "IX_repartidores_usuario_id",
                table: "repartidores",
                column: "usuario_id");

            migrationBuilder.CreateIndex(
                name: "IX_TokensRevocados_TokenHash",
                table: "TokensRevocados",
                column: "TokenHash");

            migrationBuilder.CreateIndex(
                name: "IX_TokensRevocados_UsuarioId",
                table: "TokensRevocados",
                column: "UsuarioId");

            migrationBuilder.CreateIndex(
                name: "IX_usuarios_correo",
                table: "usuarios",
                column: "correo",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_ventas_caja_id",
                table: "ventas",
                column: "caja_id");

            migrationBuilder.CreateIndex(
                name: "IX_ventas_empleado_id",
                table: "ventas",
                column: "empleado_id");

            migrationBuilder.CreateIndex(
                name: "IX_ventas_pedido_id",
                table: "ventas",
                column: "pedido_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "calificaciones");

            migrationBuilder.DropTable(
                name: "detalle_compra_insumos");

            migrationBuilder.DropTable(
                name: "detalle_pedido");

            migrationBuilder.DropTable(
                name: "inventario_log");

            migrationBuilder.DropTable(
                name: "notificaciones");

            migrationBuilder.DropTable(
                name: "TokensRevocados");

            migrationBuilder.DropTable(
                name: "ventas");

            migrationBuilder.DropTable(
                name: "compras_insumos");

            migrationBuilder.DropTable(
                name: "productos");

            migrationBuilder.DropTable(
                name: "insumos");

            migrationBuilder.DropTable(
                name: "caja");

            migrationBuilder.DropTable(
                name: "pedidos");

            migrationBuilder.DropTable(
                name: "empleados");

            migrationBuilder.DropTable(
                name: "clientes");

            migrationBuilder.DropTable(
                name: "repartidores");

            migrationBuilder.DropTable(
                name: "usuarios");
        }
    }
}
