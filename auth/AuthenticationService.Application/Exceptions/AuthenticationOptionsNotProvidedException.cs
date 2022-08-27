[System.Serializable]
public class AuthenticationOptionsNotProvidedException : System.Exception
{
    public AuthenticationOptionsNotProvidedException(string message, System.Exception? inner = null) : base(message, inner) { }
    protected AuthenticationOptionsNotProvidedException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}