using AuthenticationService.Application.Commands;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;

public class UseCases : IUseCases
{
    public ICredentialsService CredentialsService { get; }
    public IUserRepository UserRepository { get; }
    public AdminOptions AdminOptions { get; }
    public UseCases(ICredentialsService service, IUserRepository userRepository, AdminOptions adminOptions)
    {
        CredentialsService = service;
        UserRepository = userRepository;
        AdminOptions = adminOptions;
    }
    public ICommand<Token> AuthenticateAdmin(in UserCredentials credentials)
    {
        if (AdminOptions.AdminSecret is null || AdminOptions.AdminUsername is null)
            throw new AdminSecretNotProvidedException();

        return new AuthenticateAdminCommand(CredentialsService, AdminOptions.AdminSecret, AdminOptions.AdminUsername, credentials);
    }

    public ICommand<Token> AuthenticateUser(in UserCredentials credentials)
    {
        return new AuthenticateUserCommand(UserRepository, CredentialsService, credentials);
    }

    public ICommand RegisterUser(in RegisterUserForm form)
    {
        return new RegisterUserCommand(UserRepository, form.Preferences, new UserCredentials(form.Username, form.Password));
    }

    public ICommand SetRoleToTrainer(in PromoteParticipantToTrainerForm form)
    {
        return new SetRoleToTrainerCommand(UserRepository, form.OldId, form.NewId);
    }

    public ICommand UnregisterUser(in UnregisterUserForm form)
    {
        return new UnregisterUserCommand(UserRepository, form.Id);
    }

    public ICommand UpdateUser(in UpdateUserForm form)
    {
        return new UpdateUserCommand(UserRepository, form.Id, form.Preferences, new UserCredentials(form.Username, form.Password));
    }

    public ICommand<Token> Authorize(in AuthorizationForm form)
    {
        return new AuthorizeCommand(CredentialsService, form.Hash);
    }
}