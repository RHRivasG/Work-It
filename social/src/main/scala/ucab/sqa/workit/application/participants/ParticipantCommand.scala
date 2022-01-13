package ucab.sqa.workit.application.participants

sealed trait ParticipantCommand;

final case class UpdateParticipantCommand(
    id: String,
    name: String,
    preferences: List[String]
) extends ParticipantCommand

final case class CreateParticipantCommand(
    name: String,
    password: String,
    preferences: List[String]
) extends ParticipantCommand

final case class ChangeParticipantPasswordCommand(
    id: String,
    password: String
) extends ParticipantCommand

final case class DeleteParticipantCommand(
    id: String
) extends ParticipantCommand

final case class IssueRequestParticipantToTrainerCommand(
    id: String
) extends ParticipantCommand

final case class AcceptRequestParticipantToTrainerCommand(
    id: String
) extends ParticipantCommand

final case class RejectRequestParticipantToTrainerCommand(
    id: String
) extends ParticipantCommand
