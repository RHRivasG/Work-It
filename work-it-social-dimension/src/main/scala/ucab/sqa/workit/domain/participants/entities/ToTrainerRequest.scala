package ucab.sqa.workit.domain.participants.entities

import ucab.sqa.workit.domain.participants.valueobjects.ToTrainerRequestId
import java.util.UUID
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantId
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerIssuedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerApprovedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerRejectedEvent
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantName
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPassword
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPreferences

private[participants] case class ToTrainerRequest(id: ToTrainerRequestId) {
  def accept(
      name: ParticipantName,
      password: ParticipantPassword,
      preferences: ParticipantPreferences
  ) =
    ParticipantRequestToConvertToTrainerApprovedEvent(
      id,
      name,
      password,
      preferences
    )
  def reject() = ParticipantRequestToConvertToTrainerRejectedEvent(id)
}

object ToTrainerRequest {
  private def apply(id: ToTrainerRequestId, participantId: ParticipantId) =
    (
      ParticipantRequestToConvertToTrainerIssuedEvent(participantId, id),
      new ToTrainerRequest(id)
    )

  def of(participantId: ParticipantId, id: String = "") = for {
    id <- ToTrainerRequestId.of(if (id == "") UUID.randomUUID.toString else id)
  } yield ToTrainerRequest(id, participantId)

  def unsafeOf(id: String) =
    new ToTrainerRequest(
      ToTrainerRequestId.unsafeOf(id)
    )

  def unsafeOf(id: UUID) =
    new ToTrainerRequest(
      ToTrainerRequestId.unsafeOf(id)
    )
}
