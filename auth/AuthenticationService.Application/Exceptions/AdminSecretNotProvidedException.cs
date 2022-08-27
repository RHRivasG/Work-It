[System.Serializable]
public class AdminSecretNotProvidedException : System.Exception
{
    public AdminSecretNotProvidedException(System.Exception? inner = null) : base("Authentication secret not provided", inner) { }
    protected AdminSecretNotProvidedException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}