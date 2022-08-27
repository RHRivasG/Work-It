namespace AuthenticationService.Application.Models;

public readonly struct PromoteParticipantToTrainerForm
{
    public Guid OldId { get; init; }
    public Guid NewId { get; init; }
}