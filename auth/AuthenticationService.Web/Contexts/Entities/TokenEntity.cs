using System.ComponentModel.DataAnnotations.Schema;

namespace AuthenticationService.Web.Contexts.Entities;

[Table("work-it-tokens")]
public class TokenEntity {
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid OwnerId { get; set; }
    public DateTime IssuedAt { get; set; }
    public TimeSpan ExpiresIn { get; set; }
    public string Value { get; set; } = string.Empty;
    public byte[] Hash { get; set; } = Array.Empty<byte>();
}