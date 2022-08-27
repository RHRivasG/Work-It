namespace AuthenticationService.Domain.User;

public struct User
{
    public Guid Id { get; private set; }
    public UserRole Role { get; set; }
    public string Username { get; set; }
    public string Password { get; set; }
    public string[] Preferences { get; set; }
    public User(string userRole, string username, string password, string[] preferences, Guid? id = null) : this(userRole.ToRole(), username, password, preferences, id)
    {
    }
    public User(UserRole userRole, string username, string password, string[] preferences, Guid? id = null)
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
    public void PromoteToTrainer(in Guid id) {
        Id = id;
        Role = UserRole.TRAINER | UserRole.PARTICIPANT;
    }
}