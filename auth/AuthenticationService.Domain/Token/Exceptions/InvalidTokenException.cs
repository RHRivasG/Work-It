namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class InvalidTokenException : System.Exception
{
    public InvalidTokenException(System.Exception? inner = null) : base("Token expired", inner) { }
    protected InvalidTokenException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
