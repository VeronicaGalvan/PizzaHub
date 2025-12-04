using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PizzaHubAPI.Migrations
{
    /// <inheritdoc />
    public partial class FixUnidadMedidaType : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            // Cambiar el tipo de dato de unidad_medida de integer a varchar(10)
            migrationBuilder.AlterColumn<string>(
                name: "unidad_medida",
                table: "insumos",
                type: "character varying(10)",
                maxLength: 10,
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            // Revertir el cambio
            migrationBuilder.AlterColumn<int>(
                name: "unidad_medida",
                table: "insumos",
                type: "integer",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "character varying(10)",
                oldMaxLength: 10);
        }
    }
}
