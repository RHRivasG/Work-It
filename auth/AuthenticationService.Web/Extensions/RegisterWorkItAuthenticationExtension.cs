using System.Security.Claims;
using AuthenticationService.Web.Models;
using AuthenticationService.Web.Services;

namespace AuthenticationService.Web.Extensions;

public static class RegisterWorkItAuthenticationExtension
{
    public static IServiceCollection AddWorkItAuth(this IServiceCollection services) {
        services
            .AddAuthentication(options => {
                options.DefaultScheme = WorkItAuthenticationHandler.DefaultAuthenticationScheme;
                options.RequireAuthenticatedSignIn = false;
            })
            .AddScheme<WorkItAuthenticationSchemeOptions, WorkItAuthenticationHandler>(WorkItAuthenticationHandler.DefaultAuthenticationScheme, _ => {})
            .AddCookie(options => {
                options.Cookie.Name = "_workit-session";
                options.Cookie.SecurePolicy = CookieSecurePolicy.SameAsRequest;
            });

        services
            .AddAuthorization(options => {
                options.AddPolicy("WorkItPolicy", policy => policy.RequireClaim(ClaimTypes.Hash)); 
            });

        return services;
    }    
}