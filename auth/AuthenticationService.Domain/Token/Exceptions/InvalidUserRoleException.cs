namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class InvalidUserRoleException : System.Exception
{
    public InvalidUserRoleException(System.Exception? inner = null) : base("Provided role is invalid", inner) { }
    protected InvalidUserRoleException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
