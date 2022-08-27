namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class FailedToComputeHashFromValueException : System.Exception
{
    public ReadOnlyMemory<char> Value { get; }
    public FailedToComputeHashFromValueException(ReadOnlyMemory<char> value, Exception? inner = null) : base($"Could not generate hash for {value}", inner) { 
        Value = value;
    }
    protected FailedToComputeHashFromValueException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
