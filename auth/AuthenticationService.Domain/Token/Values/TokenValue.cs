namespace AuthenticationService.Domain.Token.Values;

using AuthenticationService.Domain.Token.Exceptions;

public readonly record struct TokenValue { 
    public ReadOnlyMemory<char> Value { get; }
    public TokenValue(string value) {
        if (string.IsNullOrEmpty(value) || string.IsNullOrWhiteSpace(value))
            throw new InvalidTokenValueException();


        Value = value.AsMemory();
    }
}