[System.Serializable]
public class InvalidUserRoleException : System.Exception
{
    public string ProvidedRole { get; }
    public InvalidUserRoleException(string role, System.Exception? inner = null) : base($"Invalid role given for user {role}", inner) {
        ProvidedRole = role;
    }
    protected InvalidUserRoleException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { 
            var role = string.Empty;
            if ((role = info.GetString("ProvidedRole")) is null) 
                throw new ArgumentException($"Invlaid {nameof(System.Runtime.Serialization.SerializationInfo)}, 'ProvidedRole' is null");

            ProvidedRole = role;
        }
}