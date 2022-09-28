using System.Runtime.InteropServices;
using System.Security.Cryptography;
using AuthenticationService.Domain.Token.Exceptions;

namespace AuthenticationService.Domain.Token.Values;

public readonly record struct TokenHash
{
    public ReadOnlyMemory<byte> Value { get; }
    public string HexString { get => Convert.ToHexString(Value.Span); }
    public TokenHash(byte[] tokenHash)
    {
        Value = (ReadOnlyMemory<byte>) tokenHash;
    }
    public TokenHash(TokenValue tokenValue)
    {
        using var sha = SHA512.Create();
        var byteSpan = MemoryMarshal.AsBytes(tokenValue.Value.Span);
        Memory<byte> output = new Memory<byte>(new byte[64]);
        
        if (!sha.TryComputeHash(byteSpan, output.Span, out var _))
            throw new FailedToComputeHashFromValueException();

        Value = (ReadOnlyMemory<byte>) output;
    }
}
