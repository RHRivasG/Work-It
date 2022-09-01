using AuthenticationService.Domain.Token.Exceptions;
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

    public Token(string value, Guid ownerId, DateTime? issued = null, TimeSpan? expiresIn = null, Guid? id = null) {
        var creationDate = DateTime.Now;
        
        if (id.HasValue) Id = id.Value;
        OwnerId = ownerId;
        Inner = new(value);
        IssuedAt = new(issued);
        ExpiresIn = new(expiresIn);
        Hash = new(Inner);
    }

    public void AssertValid() {
        if (!IsValid) throw new InvalidTokenException();
    }

    public bool Equals(Token other) => Hash == other.Hash;
}
