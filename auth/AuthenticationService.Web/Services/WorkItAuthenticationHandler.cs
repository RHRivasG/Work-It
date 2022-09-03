using System.Buffers.Text;
using System.Runtime.InteropServices;
using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using System.Text.Json;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Domain.Token;
using AuthenticationService.Domain.Token.Exceptions;
using AuthenticationService.Domain.User;
using AuthenticationService.Web.Contexts;
using AuthenticationService.Web.Models;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;

namespace AuthenticationService.Web.Services;

public class WorkItAuthenticationHandler : SignInAuthenticationHandler<WorkItAuthenticationSchemeOptions>
{
    public const string DefaultAuthenticationScheme = "WorkIt";

    IUseCases UseCases { get; }
    UserContext UserContext { get; }

    public WorkItAuthenticationHandler(
        IOptionsMonitor<WorkItAuthenticationSchemeOptions> options,
        ILoggerFactory logger,
        UrlEncoder encoder,
        ISystemClock clock,
        IUseCases useCases,
        UserContext userContext) : base(options, logger, encoder, clock)
    {
        UseCases = useCases;
        UserContext = userContext;
    }
    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        var tokenResult = await Context.AuthenticateAsync(CookieAuthenticationDefaults.AuthenticationScheme);

        if (!tokenResult.Succeeded)
            return AuthenticateResult.Fail("User not logged in");

        var tokenHash = (from claim in tokenResult.Principal.Claims
                         where claim.Type == ClaimTypes.Hash
                         select claim.Value).First();

        var token = await UseCases.Authorize(new() { Hash = Convert.FromHexString(tokenHash.AsSpan()) }).Run();

        if (!TrySavePrincipal(token)) return AuthenticateResult.Fail("Could not read token value");

        if (!token.IsValid)
        {
            await Context.SignOutAsync(DefaultAuthenticationScheme);
            return AuthenticateResult.Fail("Invalid token");
        }

        return AuthenticateResult.Success(new AuthenticationTicket(Context.User, new(), Scheme.Name));
    }
    protected override async Task HandleSignInAsync(ClaimsPrincipal user, AuthenticationProperties? properties)
    {
        var basicToken = (from claim in user.Claims
                          where claim.Type == ClaimTypes.Sid
                          select claim.Value).FirstOrDefault();
        var role = (from claim in user.Claims
                    where claim.Type == ClaimTypes.Role
                    select claim.Value).FirstOrDefault()?.ToRole() ?? UserRole.PARTICIPANT;

        if (!TryGetCredentials(basicToken, role, out var credentials))
#warning TODO: Failed to retrieve credentials exception
            throw new Exception();

        var command = credentials.Role is not UserRole.ADMIN ?
            UseCases.AuthenticateUser(credentials) :
            UseCases.AuthenticateAdmin(credentials);

        var token = await command.Run();

        if (!TrySavePrincipal(token)) 
            throw new MemoryAccessException("Could not retrieve token value");

        if (!token.IsValid)
        {
            await Context.SignOutAsync(DefaultAuthenticationScheme);
            Response.StatusCode = StatusCodes.Status401Unauthorized;
            return;
        }

        await Context.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, Context.User, properties);
    }
    protected override async Task HandleSignOutAsync(AuthenticationProperties? properties)
    {
        var userId = (from claim in Context.User.Claims
                      where claim.Type is ClaimTypes.NameIdentifier
                      select claim.Value).First();

        var userGuid = Guid.Parse(userId);

        var user = await UserContext.Users.Where(user => user.Id == userGuid).Include(user => user.Token).FirstAsync();

        if (user.Token is null) return;

        UserContext.Tokens.Remove(user.Token);

        user.Token = null;

        await UserContext.SaveChangesAsync();

        await Context.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme, properties);
    }
    private bool TryGetCredentials(ReadOnlySpan<char> authToken, UserRole role, out UserCredentials credentials)
    {
        try
        {
            credentials = default;

            var basicCredentials = authToken.TrimStart();

            if (!basicCredentials[..5].SequenceEqual("Basic")) return false;

            var base64Credentials = basicCredentials[6..].TrimStart();

            Span<byte> base64CredentialsAsBytes = new byte[base64Credentials.Length];

            Encoding.UTF8.GetBytes(base64Credentials, base64CredentialsAsBytes);

            Span<byte> bytes = new byte[GetOriginalLength(base64Credentials)];

            var credentialsAsBytes = Base64.DecodeFromUtf8(base64CredentialsAsBytes, bytes, out var consumed, out var written);

            var rawCredentials = Encoding.UTF8.GetString(bytes).AsMemory();

            var rawCredentialsSpan = rawCredentials.Span;

            var colonPosition = rawCredentialsSpan.IndexOf(':');

            if (colonPosition is -1) return false;

            var username = rawCredentials[..colonPosition];
            var password = rawCredentials[(colonPosition + 1)..];

            Logger.LogInformation("Got following login claims: Username = {}, Password = {}, Role = {}", username.ToString(), password.ToString(), role);

            credentials = new(username, password, role);

            return true;
        }
        catch
        {
            credentials = default;
            return false;
        }
    }
    private bool TrySavePrincipal(in Token token)
    {
        if (!MemoryMarshal.TryGetString(token.Inner.Value, out var tokenValue, out _, out _))
            return false;

        var claims = new List<Claim>() {
            new(ClaimTypes.NameIdentifier, token.OwnerId.ToString()),
            new(ClaimTypes.Sid, tokenValue),
            new(ClaimTypes.Hash, token.Hash.HexString)
        };

        var claimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims, Scheme.Name));

        Context.User = claimsPrincipal;

        return true;

    }
    private int GetOriginalLength(ReadOnlySpan<char> base64Credentials)
    {
        var lastTwoCharacters = base64Credentials[^2..];
        var base64Length = base64Credentials.Length;
        int count = 0;

        for (int i = 0; i < 2; i++)
            if (lastTwoCharacters[i] == '=')
                count++;

        return (3 * (base64Length / 4)) - count;
    }
}
