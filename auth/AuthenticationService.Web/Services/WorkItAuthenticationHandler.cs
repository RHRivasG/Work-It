using System.Buffers.Text;
using System.Runtime.InteropServices;
using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using AuthenticationService.Application.Interfaces;
using AuthenticationService.Application.Models;
using AuthenticationService.Web.Models;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.Extensions.Options;

namespace AuthenticationService.Web.Services;

public class WorkItAuthenticationHandler : AuthenticationHandler<WorkItSchemeOptions>
{
    public const string DefaultAuthenticationScheme = "WorkIt";
    IUseCases UseCases { get; }
    public WorkItAuthenticationHandler(
        IOptionsMonitor<WorkItSchemeOptions> options, 
        ILoggerFactory logger, 
        UrlEncoder encoder, 
        ISystemClock clock,
        IUseCases useCases
    ) : base(options, logger, encoder, clock)
    {
        UseCases = useCases;
    }

    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {

        if (!TryGetCredentials(out var credentials)) 
            return AuthenticateResult.Fail("Failed to retrieve credentials from Authentication header");

        var command = credentials.Role is UserRole.ADMIN ?
            UseCases.AuthenticateUser(credentials) :
            UseCases.AuthenticateAdmin(credentials);
        
        var token = await command.Run();

        var claims = new List<Claim> {
            new(ClaimTypes.Hash, token.Hash.HexString)
        };

        var claimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme));

        await Context.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, claimsPrincipal);

        var ticket = new AuthenticationTicket(claimsPrincipal, Scheme.Name);

        return AuthenticateResult.Success(ticket);
    }
    private bool TryGetCredentials(out UserCredentials credentials) {
        credentials = default;

        var basicCredentials = Request.Headers.Authorization.ToString().AsSpan().TrimStart();

        if (!basicCredentials[..6].SequenceEqual("Basic")) return false;

        var base64Credentials = basicCredentials[6..].TrimStart();

        var base64CredentialsAsBytes = MemoryMarshal.AsBytes(base64Credentials);

        Memory<byte> bytes = new Memory<byte>(new byte[GetOriginalLength(base64Credentials)]);

        var credentialsAsBytes = Base64.DecodeFromUtf8(base64CredentialsAsBytes, bytes.Span, out var consumed, out var written);

        var rawCredentials = Encoding.UTF8.GetString(bytes.Span).AsMemory();

        var rawCredentialsSpan = rawCredentials.Span;

        var colonPosition = rawCredentialsSpan.IndexOf(':');

        if (colonPosition is -1) return false;

        var username = rawCredentials[..colonPosition];
        var password = rawCredentials[colonPosition..];
        var role = Request.Headers["X-Role"].ToString().ToRole();

        credentials = new(username, password, role);

        return true;
    }
    private int GetOriginalLength(ReadOnlySpan<char> base64Credentials) {
        var lastTwoCharacters = base64Credentials[^2..];
        var base64Length = base64Credentials.Length;
        int count = 0;

        for (int i = 0; i < 2; i++)
            if (lastTwoCharacters[0] == '=')
                count++;

        return (3 * (base64Length / 4)) - count;
    }
}