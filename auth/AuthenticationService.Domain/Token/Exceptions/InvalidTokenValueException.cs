namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class InvalidTokenValueException : System.Exception
{
    public InvalidTokenValueException(System.Exception? inner = null) : base("The token value cannot be empty or null", inner) { }
    protected InvalidTokenValueException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}