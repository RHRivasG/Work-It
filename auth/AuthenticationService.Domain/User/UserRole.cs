using AuthenticationService.Domain.Token.Exceptions;

namespace AuthenticationService.Domain.User;

[Flags]
public enum UserRole {
    TRAINER = 1,
    PARTICIPANT = 2,
    ADMIN = 4
}

public static class UserRoles {
    public static UserRole ToRole(this string value) {
        return value switch {
            "trainer" => UserRole.TRAINER | UserRole.PARTICIPANT,
            "participant" => UserRole.PARTICIPANT,
            "admin" => UserRole.ADMIN,
            _ => throw new InvalidUserRoleException()
        };
    }
    public static UserRole ToRole(this string[] values) {
        if (values is { Length: 0 }) 
            throw new InvalidUserRolesException();

        return values.Select(UserRoles.ToRole).Aggregate((a, b) => a | b);
    }
    public static string[] ToArray(this UserRole? role) {
        if (!role.HasValue) return Array.Empty<string>();

        return ToArray(role.Value);
    }
    public static string[] ToArray(this UserRole role) {
        var roles = new List<string>();

        if (role.HasFlag(UserRole.PARTICIPANT))
            roles.Add("participant");

        if (role.HasFlag(UserRole.TRAINER))
            roles.Add("trainer");

        if (role.HasFlag(UserRole.ADMIN))
            roles.Add("admin");

        return roles.ToArray();
    }
}
