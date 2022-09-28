using System.Runtime.InteropServices;
using AuthenticationService.Domain.User;

namespace AuthenticationService.Application.Models;

public readonly record struct UserCredentials
{
    public UserCredentials(ReadOnlyMemory<char> username, ReadOnlyMemory<char> password, UserRole? userRole = null)
    {
        Username = username;
        Password = password;
        Role = userRole;
    }
    public UserCredentials(string username, string password, UserRole? userRole = null): this(username.AsMemory(), password.AsMemory(), userRole) {}
    public ReadOnlyMemory<char> Username { get; init; }
    public ReadOnlyMemory<char> Password { get; init; }
    public UserRole? Role { get; init; }
    public string GetUsername() {
        return Username.ToString();
    }
    public string GetPassword() {
        return Password.ToString();
    }
}