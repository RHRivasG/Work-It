using System.ComponentModel.DataAnnotations.Schema;

namespace AuthenticationService.Web.Contexts.Entities;

[Table("work-it-users")]
public class UserEntity
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string[] Role { get; set; } = Array.Empty<string>();
    public string[] Preferences { get; set; } = Array.Empty<string>();
    public bool Deleted { get; set; } = false;
    public TokenEntity? Token { get; set; } = null;
}