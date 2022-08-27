[System.Serializable]
public class InvalidPasswordException : System.Exception
{
    public string Password { get; }
    public InvalidPasswordException(string password, System.Exception? inner = null) : base($"Invalid password {password}", inner) {
        Password = password;
    }
    protected InvalidPasswordException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) {
            Password = info.GetString(nameof(Password))!;

        }
}