using System.Text;
using System.Buffers.Text;
using AuthenticationService.Application.Models;
using AuthenticationService.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System.Runtime.InteropServices;
using Microsoft.AspNetCore.Authentication;
using AuthenticationService.Web.Services;

namespace AuthenticationService.Web.Controllers;

[ApiController]
[Route("[controller]")]
public class LoginController : ControllerBase {
    [HttpPost()]
    public async Task<ActionResult> LoginParticipant() {
        await HttpContext.AuthenticateAsync();

        return Ok();
    }
}