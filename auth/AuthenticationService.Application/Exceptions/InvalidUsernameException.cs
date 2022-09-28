[System.Serializable]
public class InvalidUsernameException : System.Exception
{
    public ReadOnlyMemory<char> Username { get; }
    public InvalidUsernameException(ReadOnlyMemory<char> username, System.Exception? inner = null) : base($"Invalid username {username}", inner) {
        Username = username;
    }
    protected InvalidUsernameException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) {
            Username = info.GetString(nameof(Username))!.AsMemory();
        }
}