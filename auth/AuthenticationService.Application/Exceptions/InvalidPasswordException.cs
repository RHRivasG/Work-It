[System.Serializable]
public class InvalidPasswordException : System.Exception
{
    public ReadOnlyMemory<char> Password { get; }
    public InvalidPasswordException(ReadOnlyMemory<char> password, System.Exception? inner = null) : base($"Invalid password {password}", inner) {
        Password = password;
    }
    protected InvalidPasswordException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) {
            Password = info.GetString(nameof(Password))!.AsMemory();

        }
}