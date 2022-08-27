[System.Serializable]
public class InvalidUserRolesException : System.Exception
{
    public InvalidUserRolesException(System.Exception? inner = null) : base("The provided roles are empty", inner) { }
    protected InvalidUserRolesException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}