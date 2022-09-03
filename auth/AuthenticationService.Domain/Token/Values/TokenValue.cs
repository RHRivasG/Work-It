namespace AuthenticationService.Domain.Token.Values;

public readonly record struct TokenValue
{
  public ReadOnlyMemory<char> Value { get; }

  public TokenValue(string value)
  {
    Value = value.AsMemory();
  }
}
