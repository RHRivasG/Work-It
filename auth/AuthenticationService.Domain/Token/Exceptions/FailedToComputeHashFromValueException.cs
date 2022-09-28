namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class FailedToComputeHashFromValueException : System.Exception
{
    public FailedToComputeHashFromValueException(System.Exception? inner = null) : base("Failed to compute hash for token", inner) { }
    protected FailedToComputeHashFromValueException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
