using System.Runtime.Serialization;
namespace AuthenticationService.Domain.Token.Exceptions;

[Serializable]
public class InvalidTokenExpirationExceptionException : Exception
{
    public InvalidTokenExpirationExceptionException(TimeSpan expiration, Exception? inner = null) : base($"Expiration {expiration} must be at least 10 minutes", inner) { }
    protected InvalidTokenExpirationExceptionException(SerializationInfo info, StreamingContext context) : base(info, context) { }
}