using AuthenticationService.Domain.Token.Values;

namespace AuthenticationService.Domain.Token;

public readonly struct Token : IEquatable<Token>
{
    public Guid Id { get; } = Guid.NewGuid();
    public Guid OwnerId { get; }
    public TokenValue Inner { get; }
    public TokenIssuedAt IssuedAt { get; }
    public TokenExpiresIn ExpiresIn { get; }
    public TokenHash Hash { get; }
    public bool IsValid { get => DateTime.Now <= IssuedAt.Value.Add(ExpiresIn.Value); }
    public Token(string value, Guid ownerId, DateTime? issued = null, TimeSpan? expiresIn = null) {
        var creationDate = DateTime.Now;
        
        OwnerId = ownerId;
        Inner = new(value);
        IssuedAt = new(issued);
        ExpiresIn = new(expiresIn);
        Hash = new(Inner);
    }

    public bool Equals(Token other) => Hash == other.Hash;
}