namespace AuthenticationService.Domain.Token.Exceptions;

[System.Serializable]
public class InvalidUserRolesException : System.Exception
{
    public InvalidUserRolesException(System.Exception? inner = null) : base("Provided roles are invalid", inner) { }
    protected InvalidUserRolesException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
