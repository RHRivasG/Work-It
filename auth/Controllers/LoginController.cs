using System.Text;
using System.Text.RegularExpressions;
using AuthenticationService.Models;
using AuthenticationService.Services;
using Microsoft.AspNetCore.Mvc;

namespace AuthenticationService.Controllers;

[ApiController]
[Route("[controller]")]
public class LoginController : ControllerBase {
    string? Username { get; set; }
    string? Password { get; set; }
    IUseCases UseCases { get; set; }
    public LoginController(IUseCases useCases) {
        UseCases = useCases;
    }
    [HttpPost("Participant")]
    public Task<ActionResult<string>> LoginParticipant() {
        return AuthenticateWithRole("participant");
    }
    [HttpPost("Trainer")]
    public Task<ActionResult<string>> LoginTrainer() {
        return AuthenticateWithRole("trainer");
    }
    [HttpPost("Admin/{token?}")]
    public async Task<ActionResult<string>> LoginAdmin(string? token) {
        if (token == null) return BadRequest();

        var credentials = await UseCases.AuthenticateAdmin(new AuthenticateAdminForm(InputToken: token));

        if (credentials == null) return Unauthorized();

        return credentials;
    }
    async Task<ActionResult<string>> AuthenticateWithRole(string role) {
        ObtainBasicCredentials(); 

        if (Username == null || Password == null) return BadRequest();
        
        var credentials = await UseCases.AuthenticateUser(new AuthenticateUserForm(Name: Username, Password: Password, Role: role));
        
        if (credentials == null) return Unauthorized();
        
        return credentials;
    }
    void ObtainBasicCredentials() {
        var basicCredentials = Request.Headers.Authorization;
        var basicRegex = new Regex("Basic (.*)");

        if (!basicRegex.IsMatch(basicCredentials )) return;

        var base64Credentials = basicRegex.Replace(basicCredentials, "$1");
        var rawCredentials = Encoding.UTF8.GetString(Convert.FromBase64String(base64Credentials));
        var credentialsArray = rawCredentials.Split(':');

        if (credentialsArray.Length != 2) return;

        Username = credentialsArray[0];
        Password = credentialsArray[1];
    }
}