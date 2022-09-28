namespace AuthenticationService.Application.Exceptions;

[System.Serializable]
public class InvalidAdminOptionsException : System.Exception
{
    public InvalidAdminOptionsException(string propertyNotProvided, System.Exception? inner = null) : base($"Invalid options, \"{propertyNotProvided}\" not provided", inner) { }
    protected InvalidAdminOptionsException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
