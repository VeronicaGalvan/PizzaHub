using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PizzaHubAPI.Migrations
{
    /// <inheritdoc />
    public partial class DB3 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "ClienteId",
                table: "Usuarios",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "RepartidorId",
                table: "Usuarios",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Usuarios_ClienteId",
                table: "Usuarios",
                column: "ClienteId");

            migrationBuilder.CreateIndex(
                name: "IX_Usuarios_RepartidorId",
                table: "Usuarios",
                column: "RepartidorId");

            migrationBuilder.AddForeignKey(
                name: "FK_Usuarios_Clientes_ClienteId",
                table: "Usuarios",
                column: "ClienteId",
                principalTable: "Clientes",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Usuarios_Repartidores_RepartidorId",
                table: "Usuarios",
                column: "RepartidorId",
                principalTable: "Repartidores",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Usuarios_Clientes_ClienteId",
                table: "Usuarios");

            migrationBuilder.DropForeignKey(
                name: "FK_Usuarios_Repartidores_RepartidorId",
                table: "Usuarios");

            migrationBuilder.DropIndex(
                name: "IX_Usuarios_ClienteId",
                table: "Usuarios");

            migrationBuilder.DropIndex(
                name: "IX_Usuarios_RepartidorId",
                table: "Usuarios");

            migrationBuilder.DropColumn(
                name: "ClienteId",
                table: "Usuarios");

            migrationBuilder.DropColumn(
                name: "RepartidorId",
                table: "Usuarios");
        }
    }
}
