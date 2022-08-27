[System.Serializable]
public class InvalidUsernameException : System.Exception
{
    public string Username { get; }
    public InvalidUsernameException(string username, System.Exception? inner = null) : base($"Invalid username {username}", inner) {
        Username = username;
    }
    protected InvalidUsernameException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) {
            Username = info.GetString(nameof(Username))!;
        }
}