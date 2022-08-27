using AuthenticationService.Application.Models;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Interfaces;

public interface IUserRepository
{
    Task<User> FindAsync(Guid id);
    Task<User> FindAsync(in UserCredentials credentials);
    Task CreateAsync(User user);
    Task SaveAsync(User user);
    Task DeleteAsync(Guid id);
    Task UndoDeleteAsync(Guid id);
}