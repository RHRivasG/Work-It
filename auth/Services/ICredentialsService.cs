namespace AuthenticationService.Services;

public interface ICredentialsService {
    Task<string> CredentialsFor(string id, string name, string[] preferences, string[] roles);
}