using AuthenticationService.Application.Models;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Interfaces;

public interface IUserRepository
{
    IUserRetrievalBuilder FindAsync(Guid id);
    IUserRetrievalBuilder FindAsync(UserCredentials credentials);
    Task CreateAsync(User user);
    Task SaveAsync(User user);
    Task DeleteAsync(Guid id);
    Task UndoDeleteAsync(Guid id);
}
