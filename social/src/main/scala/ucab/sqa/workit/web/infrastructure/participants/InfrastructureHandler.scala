package ucab.sqa.workit.web.infrastructure.participants

import cats._
import cats.implicits._
import scala.concurrent.Future
import scala.util.Try
import java.util.UUID
import ucab.sqa.workit.application.participants.GetParticipant
import ucab.sqa.workit.application.participants.ParticipantAction
import ucab.sqa.workit.application.participants.GetAllParticipants
import ucab.sqa.workit.domain.participants.Participant
import ucab.sqa.workit.domain.participants.ParticipantEvent
import ucab.sqa.workit.application.participants.Handle
import ucab.sqa.workit.domain.participants.ParticipantCreatedEvent
import ucab.sqa.workit.application.participants.CreateParticipantCommand
import ucab.sqa.workit.domain.participants.ParticipantUpdatedEvent
import ucab.sqa.workit.domain.participants.ParticipantPasswordChangedEvent
import ucab.sqa.workit.domain.participants.ParticipantDeletedEvent
import ucab.sqa.workit.domain.participants.ParticipantPreferencesAdded
import ucab.sqa.workit.domain.participants.ParticipantPreferencesRemoved
import ucab.sqa.workit.application.participants.GetAllPreferences
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerIssuedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerApprovedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerRejectedEvent
import ucab.sqa.workit.application.participants.GetParticipantWithUsername

case class InfrastructureHandler(
    findParticipant: UUID => Future[Either[Error, Participant]],
    findParticipantByName: String => Future[Either[Error, Participant]],
    findParticipants: () => Future[Either[Error, List[Participant]]],
    findPreferences: () => Future[Either[Error, List[Preference]]],
    createHandler: (
        UUID,
        String,
        String,
        List[String]
    ) => Future[Either[Error, Unit]],
    updateHandler: (UUID, String) => Future[Either[Error, Unit]],
    preferencesAddedHandler: (
        UUID,
        List[String]
    ) => Future[Either[Error, Unit]],
    preferencesRemovedHandler: (
        UUID,
        List[String]
    ) => Future[Either[Error, Unit]],
    passwordChangedHandler: (
        UUID,
        String
    ) => Future[Either[Error, Unit]],
    deleteHandler: (UUID) => Future[Either[Error, Unit]],
    requestIssuedHandler: (UUID, UUID) => Future[Either[Error, Unit]],
    requestApprovedHandler: (
        UUID,
        UUID,
        String,
        String,
        List[String]
    ) => Future[Either[Error, Unit]],
    requestRejectedHandler: (UUID) => Future[Either[Error, Unit]]
) extends (ParticipantAction ~> Future) {
  def apply[A](evt: ParticipantAction[A]) = evt match {
    case GetParticipant(id)                   => findParticipant(id)
    case GetParticipantWithUsername(username) => findParticipantByName(username)
    case GetAllParticipants()                 => findParticipants()
    case GetAllPreferences()                  => findPreferences()
    case Handle(ParticipantCreatedEvent(id, name, password, preferences)) =>
      createHandler(
        id.id,
        name.name,
        password.password,
        preferences.preferences.map(_.tag)
      )
    case Handle(ParticipantUpdatedEvent(id, name)) =>
      updateHandler(
        id.id,
        name.name
      )
    case Handle(ParticipantPreferencesAdded(id, preferences)) =>
      preferencesAddedHandler(
        id.id,
        preferences.preferences map { _.tag }
      )
    case Handle(ParticipantPreferencesRemoved(id, preferences)) =>
      preferencesRemovedHandler(
        id.id,
        preferences.preferences map { _.tag }
      )
    case Handle(ParticipantPasswordChangedEvent(id, password)) =>
      passwordChangedHandler(
        id.id,
        password.password
      )
    case Handle(ParticipantDeletedEvent(id)) =>
      deleteHandler(id.id)
    case Handle(
          ParticipantRequestToConvertToTrainerIssuedEvent(id, requestId)
        ) =>
      requestIssuedHandler(id.id, requestId.id)
    case Handle(
          ParticipantRequestToConvertToTrainerApprovedEvent(
            id,
            participantId,
            name,
            password,
            preferences
          )
        ) =>
      requestApprovedHandler(
        id.id,
        participantId.id,
        name.name,
        password.password,
        preferences.preferences.map(_.tag)
      )
    case Handle(
          ParticipantRequestToConvertToTrainerRejectedEvent(
            id
          )
        ) =>
      requestRejectedHandler(id.id)
  }
}
