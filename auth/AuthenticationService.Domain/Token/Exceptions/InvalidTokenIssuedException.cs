namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class InvalidTokenIssuedExceptionException : System.Exception
{
    public InvalidTokenIssuedExceptionException(DateTime issued, System.Exception? inner = null) : base($"Issued datetime {issued} cannot be before current date", inner) { }
    protected InvalidTokenIssuedExceptionException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}