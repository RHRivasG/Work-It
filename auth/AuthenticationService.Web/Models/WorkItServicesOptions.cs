namespace AuthenticationService.Web.Models;

public class WorkItServicesOptions
{
    public AdminOptions Admin { get; } = new();
    public AuthenticationOptions Authentication { get; } = new();

    public string ConnectionString { get; set; } = string.Empty;
}