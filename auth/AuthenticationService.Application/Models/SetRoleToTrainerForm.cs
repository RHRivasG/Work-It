namespace AuthenticationService.Application.Models;

public readonly record struct SetRoleToTrainerForm {
  public Guid NewId { get; init; }
  public Guid OldId { get; init; }
}
