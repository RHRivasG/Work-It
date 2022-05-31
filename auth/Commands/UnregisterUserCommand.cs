using AuthenticationService.Contexts;

namespace AuthenticationService.Commands;

class UnregisterUserCommand: ICommand {
    public UserContext UserContext { private get; init; }
    public Guid Id { private get; init; }
    private User? DeletedUser { get; set; } = null;
    public async Task Rollback()
    {
        if (DeletedUser == null) return;
        
        await UserContext.Users.AddAsync(DeletedUser);
        await UserContext.SaveChangesAsync();
    }
    public async Task Run()
    {
        var user = await UserContext.Users.FindAsync(Id);

        if (user == null) return;

        DeletedUser = new User {
            Id = user.Id,
            Name = user.Name,
            Password = user.Password,
            Role = user.Role
        };
        
        UserContext.Users.Remove(user);
        await UserContext.SaveChangesAsync();
    }
}