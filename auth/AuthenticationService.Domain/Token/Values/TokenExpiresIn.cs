namespace AuthenticationService.Domain.Token.Values;

public readonly record struct TokenExpiresIn
{
    public TimeSpan Value { get; }

    public TokenExpiresIn(TimeSpan? expiresIn = null)
    {
        Value = expiresIn ?? TimeSpan.FromHours(1);
    }
}