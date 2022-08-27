using System.Runtime.InteropServices;

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
        MemoryMarshal.TryGetString(Username, out var username, out _, out _);

        if (username is null)
            throw new MemoryAccessException("Memory failure while accessing username");

        return username;
    }
    public string GetPassword() {
        MemoryMarshal.TryGetString(Password, out var password, out _, out _);

        if (password is null)
            throw new MemoryAccessException("Memory failure while accessing password");

        return password;
    }
}