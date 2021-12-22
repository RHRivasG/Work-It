package ucab.sqa.workit.web.infrastructure.database

import ucab.sqa.workit.domain.participants.Participant
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import akka.actor.typed.ActorRef
import java.util.UUID
import ucab.sqa.workit.domain.trainers.Trainer

object Request {
  private[database] sealed trait DatabaseRequest

  sealed trait ParticipantDatabaseRequest extends DatabaseRequest
  sealed trait TrainerDatabaseRequest extends DatabaseRequest

  final case class GetParticipant(
      id: UUID,
      replyTo: ActorRef[Either[Error, Participant]]
  ) extends ParticipantDatabaseRequest

  final case class GetParticipantWithUsername(
      username: String,
      replyTo: ActorRef[Either[Error, Participant]]
  ) extends ParticipantDatabaseRequest

  final case class GetParticipants(
      replyTo: ActorRef[Either[Error, List[Participant]]]
  ) extends ParticipantDatabaseRequest

  final case class GetPreferences(
      replyTo: ActorRef[Either[Error, List[Preference]]]
  ) extends ParticipantDatabaseRequest

  final case class CreateParticipant(
      id: UUID,
      name: String,
      password: String,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class UpdateParticipant(
      id: UUID,
      name: String,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class ChangeParticipantPassword(
      id: UUID,
      password: String,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class DeleteParticipant(
      id: UUID,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class AddParticipantPreferences(
      id: UUID,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class RemoveParticipantPreferences(
      id: UUID,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class IssueParticipantRequest(
      id: UUID,
      requestId: UUID,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class AcceptParticipantRequest(
      id: UUID,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class RejectParticipantRequest(
      id: UUID,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends ParticipantDatabaseRequest

  final case class GetTrainer(
      id: UUID,
      replyTo: ActorRef[Either[Error, Trainer]]
  ) extends TrainerDatabaseRequest

  final case class GetTrainerWithUsername(
      username: String,
      replyTo: ActorRef[Either[Error, Trainer]]
  ) extends TrainerDatabaseRequest

  final case class GetTrainers(
      replyTo: ActorRef[Either[Error, List[Trainer]]]
  ) extends TrainerDatabaseRequest

  final case class CreateTrainer(
      id: UUID,
      name: String,
      password: String,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

  final case class UpdateTrainer(
      id: UUID,
      name: String,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

  final case class ChangeTrainerPassword(
      id: UUID,
      password: String,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

  final case class DeleteTrainer(
      id: UUID,
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

  final case class AddTrainerPreferences(
      id: UUID,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

  final case class RemoveTrainerPreferences(
      id: UUID,
      preferences: List[String],
      replyTo: ActorRef[Either[Error, Unit]]
  ) extends TrainerDatabaseRequest

}
