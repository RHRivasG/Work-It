using AuthenticationService.Commands;
using AuthenticationService.Contexts;
using AuthenticationService.Models;

namespace AuthenticationService.Services;

class UseCases : IUseCases {
    ICredentialsService CredentialsService { get; set; }
    UserContext UserContext { get; set; }
    IConfiguration Configuration { get; set; }
    public UseCases(
        ICredentialsService credentialsService, 
        UserContext userContext,
        IConfiguration configuration
    ) {
        CredentialsService = credentialsService;
        UserContext = userContext;
        Configuration = configuration;
    }
    protected virtual async Task<T> RunCommand<T>(ICommand<T> Command) {
        try {
            return await Command.Run();
        } catch (Exception e) {
            await Command.Rollback();
            throw e;
        }
    }
    protected virtual async Task RunCommand(ICommand Command) {
        try {
            await Command.Run();
        } catch (Exception e) {
            await Command.Rollback();
            throw e;
        }
    }

    public Task RegisterUser(RegisterUserForm Form)
    {
        return RunCommand(new RegisterUserCommand {
            UserContext = UserContext,
            User = new User {
                Id = Form.Id,
                Name = Form.Name,
                Password = Form.Password,
                Role = Form.Role,
                Preferences = Form.Preferences
            }
        });
    }

    public Task UpdateUser(UpdateUserForm Form)
    {
        return RunCommand(new UpdateUserCommand {
            UserContext = UserContext,
            User = new User {
                Id = Form.Id,
                Name = Form.Name,
                Password = Form.Password,
                Role = Form.Role,
                Preferences = Form.Preferences
            }
        });
    }

    public Task UnregisterUser(UnregisterUserForm Form)
    {
        return RunCommand(new UnregisterUserCommand {
            UserContext = UserContext,
            Id = Form.Id 
        });
    }

    public Task<string?> AuthenticateUser(AuthenticateUserForm Form)
    {
        return RunCommand(new AuthenticateUserCommand {
            UserContext = UserContext,
            CredentialsService = CredentialsService,
            Name = Form.Name,
            Password = Form.Password,
            Role = Form.Role
        });
    }

    public Task<string?> AuthenticateAdmin(AuthenticateAdminForm Form)
    {
        return RunCommand(new AuthenticateAdminCommand {
            CredentialsService = CredentialsService,
            Password = Form.InputToken,
            AdminSecret = Configuration["Admin:Secret"]
        });
    }
    
    public Task SetRoleToTrainer(SetRoleToTrainerForm Form) 
    {
        return RunCommand(new SetRoleToTrainerCommand {
            Id = Form.Id,
            NewId = Form.NewId
        });
    }
}