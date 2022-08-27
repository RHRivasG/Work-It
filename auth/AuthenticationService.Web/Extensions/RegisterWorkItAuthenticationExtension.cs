using System.Security.Claims;
using AuthenticationService.Web.Models;
using AuthenticationService.Web.Services;

namespace AuthenticationService.Web.Extensions;

public static class RegisterWorkItAuthenticationExtension
{
    public static IServiceCollection AddWorkItAuth(this IServiceCollection services) {
        services
            .AddAuthentication(options => {
                options.DefaultAuthenticateScheme = WorkItAuthenticationHandler.DefaultAuthenticationScheme;
            })
            .AddCookie(options => {
                options.Cookie.Name = "_workit-session";
                options.Cookie.Domain = "/";
                options.Cookie.HttpOnly = true;
            })
            .AddScheme<WorkItSchemeOptions, WorkItAuthenticationHandler>(WorkItAuthenticationHandler.DefaultAuthenticationScheme, _ => {})
            .AddScheme<WorkItTokenSchemeOptions, WorkItTokenAuthenticationHandler>(WorkItTokenAuthenticationHandler.DefaultAuthenticationScheme, _ => {});

        services
            .AddAuthorization(options => {
                options.AddPolicy("WorkItPolicy", policy => policy.RequireClaim(ClaimTypes.Hash)); 
            });

        return services;
    }    
}