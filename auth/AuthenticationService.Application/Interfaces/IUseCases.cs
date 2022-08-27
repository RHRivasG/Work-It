using AuthenticationService.Application.Commands;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;

namespace AuthenticationService.Application.Interfaces;

public interface IUseCases {
    ICommand RegisterUser(in RegisterUserForm form); 
    ICommand UpdateUser(in UpdateUserForm form); 
    ICommand SetRoleToTrainer(in PromoteParticipantToTrainerForm form);
    ICommand UnregisterUser(in UnregisterUserForm form); 
    ICommand<Token> Authorize(in AuthorizationForm form);
    ICommand<Token> AuthenticateUser(in UserCredentials credentials); 
    ICommand<Token> AuthenticateAdmin(in UserCredentials credentials);
}