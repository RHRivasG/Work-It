[System.Serializable]
public class InvalidUserRoleException : System.Exception
{
    public InvalidUserRoleException(System.Exception? inner = null) : base("Invalid role", inner) { }
    protected InvalidUserRoleException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}