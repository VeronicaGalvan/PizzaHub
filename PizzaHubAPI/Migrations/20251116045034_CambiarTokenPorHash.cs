using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PizzaHubAPI.Migrations
{
    /// <inheritdoc />
    public partial class CambiarTokenPorHash : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_TokensRevocados_Token",
                table: "TokensRevocados");

            migrationBuilder.DropColumn(
                name: "Token",
                table: "TokensRevocados");

            migrationBuilder.AddColumn<string>(
                name: "TokenHash",
                table: "TokensRevocados",
                type: "varchar(64)",
                maxLength: 64,
                nullable: false,
                defaultValue: "")
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "IX_TokensRevocados_TokenHash",
                table: "TokensRevocados",
                column: "TokenHash");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_TokensRevocados_TokenHash",
                table: "TokensRevocados");

            migrationBuilder.DropColumn(
                name: "TokenHash",
                table: "TokensRevocados");

            migrationBuilder.AddColumn<string>(
                name: "Token",
                table: "TokensRevocados",
                type: "varchar(255)",
                nullable: false,
                defaultValue: "")
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "IX_TokensRevocados_Token",
                table: "TokensRevocados",
                column: "Token");
        }
    }
}
