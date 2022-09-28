[System.Serializable]
public class InvalidAuthenticationOptionsException : System.Exception
{
    public InvalidAuthenticationOptionsException(string propertyNotProvided, System.Exception? inner = null) : base($"Invalid authentication options, missing {propertyNotProvided}", inner) { }
    protected InvalidAuthenticationOptionsException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
