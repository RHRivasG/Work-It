using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AuthenticationService.Web.Migrations
{
    public partial class AddedPrimaryKeyToToken : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "work-it-tokens",
                columns: table => new
                {
                    OwnerId = table.Column<string>(type: "TEXT", nullable: false),
                    Id = table.Column<string>(type: "TEXT", nullable: false),
                    IssuedAt = table.Column<DateTime>(type: "TEXT", nullable: false),
                    ExpiresIn = table.Column<TimeSpan>(type: "TEXT", nullable: false),
                    Value = table.Column<string>(type: "TEXT", nullable: false),
                    Hash = table.Column<string>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_work-it-tokens", x => x.OwnerId);
                });

            migrationBuilder.CreateTable(
                name: "work-it-users",
                columns: table => new
                {
                    Id = table.Column<string>(type: "TEXT", nullable: false),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    Password = table.Column<string>(type: "TEXT", nullable: false),
                    Role = table.Column<string>(type: "TEXT", nullable: false),
                    Preferences = table.Column<string>(type: "TEXT", nullable: false),
                    Deleted = table.Column<bool>(type: "INTEGER", nullable: false),
                    TokenOwnerId = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_work-it-users", x => x.Id);
                    table.ForeignKey(
                        name: "FK_work-it-users_work-it-tokens_TokenOwnerId",
                        column: x => x.TokenOwnerId,
                        principalTable: "work-it-tokens",
                        principalColumn: "OwnerId");
                });

            migrationBuilder.CreateIndex(
                name: "IX_work-it-tokens_Hash",
                table: "work-it-tokens",
                column: "Hash");

            migrationBuilder.CreateIndex(
                name: "IX_work-it-tokens_Id",
                table: "work-it-tokens",
                column: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_work-it-users_TokenOwnerId",
                table: "work-it-users",
                column: "TokenOwnerId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "work-it-users");

            migrationBuilder.DropTable(
                name: "work-it-tokens");
        }
    }
}
