using AuthenticationService.Web.Middelwares;
namespace AuthenticationService.Web.Extensions;

public static class RegisterWorkItExceptionHandler
{
    public static void UseWorkItExceptionHandler(this WebApplication application) {
        application.UseMiddleware<ErrorHandlerMiddleware>();
    }
}