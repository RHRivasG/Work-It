using AuthenticationService.Contexts;

namespace AuthenticationService.Commands;

public class SetRoleToTrainerCommand : ICommand {
    public UserContext UserContext { private get; init; } 
    public Guid Id { private get; init; }
    public Guid NewId { private get; init; }
    bool Updated { get; set; } = false;
    public async Task Rollback()
    {
        if (!Updated) return;

        var user = await UserContext.Users.FindAsync(Id);

        if (user == null) return;

        user.Id = Id;

        user.Role = "participant";
        
        await UserContext.SaveChangesAsync();
    }
    public async Task Run()
    {
        var user = await UserContext.Users.FindAsync(Id);

        if (user == null) return;

        user.Id = NewId;

        user.Role = "trainer";
        
        await UserContext.SaveChangesAsync();
        
        Updated = true;
    }
}