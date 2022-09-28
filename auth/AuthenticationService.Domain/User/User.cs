namespace AuthenticationService.Domain.User;

public class User
{
    public Guid Id { get; private set; }
    public UserRole Role { get; set; }
    public ReadOnlyMemory<char> Username { get; set; }
    public ReadOnlyMemory<char> Password { get; set; }
    public string[] Preferences { get; set; }
    public User(string userRole, string username, string password, string[] preferences, Guid? id = null) : 
        this(userRole.ToRole(), username.AsMemory(), password.AsMemory(), preferences, id) {}
    public User(UserRole userRole, ReadOnlyMemory<char> username, ReadOnlyMemory<char> password, string[] preferences, Guid? id = null)
    {
        Id = id ?? Guid.NewGuid();
        Role = userRole;
        Username = username;
        Password = password;
        Preferences = preferences;
    }
    public bool Is(UserRole queryRole) {
        return Role.HasFlag(queryRole);
    }
    public void PromoteToTrainer(Guid id) {
        Id = id;
        Role = UserRole.TRAINER | UserRole.PARTICIPANT;
    }
}
