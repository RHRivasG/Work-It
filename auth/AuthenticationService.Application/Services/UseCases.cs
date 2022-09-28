using AuthenticationService.Application.Commands;
using AuthenticationService.Application.Exceptions;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Services;

public class UseCases : IUseCases
{
    public ICredentialsService CredentialsService { get; }
    public IUserRepository UserRepository { get; }
    public AdminOptions AdminOptions { get; }

    public UseCases(ICredentialsService credentialsService, IUserRepository userRepository, AdminOptions options)
    {
        CredentialsService = credentialsService;
        UserRepository = userRepository;
        AdminOptions = options;
    }

    public ICommand<Token> AuthenticateAdmin(UserCredentials credentials)
    {
        return new AuthenticateAdminCommand(
            CredentialsService,
            AdminOptions.AdminSecret ?? throw new InvalidAdminOptionsException(nameof(AdminOptions.AdminSecret)),
            AdminOptions.AdminUsername ?? throw new InvalidAdminOptionsException(nameof(AdminOptions.AdminUsername)),
            credentials
          );
    }

    public ICommand<Token> AuthenticateUser(UserCredentials credentials)
    {
        return new AuthenticateUserCommand(
            UserRepository,
            CredentialsService,
            credentials
          );
    }

    public ICommand<Token> Authorize(AuthorizationForm form)
    {
        return new AuthorizeCommand(CredentialsService, form.Hash);
    }

    public ICommand RegisterUser(RegisterUserForm form)
    {
        var credentials = new UserCredentials(form.Name.AsMemory(), form.Password.AsMemory(), form.Role.ToRole());

        return new RegisterUserCommand(
            UserRepository,
            form.Preferences,
            credentials,
            form.Id
          );
    }

    public ICommand SetRoleToTrainer(SetRoleToTrainerForm form)
    {
        return new SetRoleToTrainerCommand(
            userRepository: UserRepository,
            newId: form.NewId,
            oldId: form.OldId
          );
    }

    public ICommand UnregisterUser(UnregisterUserForm form)
    {
        return new UnregisterUserCommand(UserRepository, form.Id);
    }

    public ICommand UpdateUser(UpdateUserForm form)
    {
        var credentials = new UserCredentials(form.Name.AsMemory(), form.Password.AsMemory(), form.Role.ToRole());

        return new UpdateUserCommand(
                UserRepository, 
                form.Id, 
                form.Preferences, 
                credentials
            );
    }
}
