package ucab.sqa.workit.domain.participants

import ucab.sqa.workit.domain.participants.valueobjects.ParticipantId
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantName
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPreferences
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPassword
import ucab.sqa.workit.domain.participants.valueobjects.ToTrainerRequestId

sealed trait ParticipantEvent

final case class ParticipantUpdatedEvent(
    id: ParticipantId,
    name: ParticipantName
) extends ParticipantEvent

final case class ParticipantPreferencesAdded(
    id: ParticipantId,
    preferences: ParticipantPreferences
) extends ParticipantEvent

final case class ParticipantPreferencesRemoved(
    id: ParticipantId,
    preferences: ParticipantPreferences
) extends ParticipantEvent

final case class ParticipantCreatedEvent(
    id: ParticipantId,
    name: ParticipantName,
    password: ParticipantPassword,
    preferences: ParticipantPreferences
) extends ParticipantEvent

final case class ParticipantDeletedEvent(
    id: ParticipantId
) extends ParticipantEvent

final case class ParticipantPasswordChangedEvent(
    id: ParticipantId,
    password: ParticipantPassword
) extends ParticipantEvent

final case class ParticipantRequestToConvertToTrainerIssuedEvent(
    id: ParticipantId,
    requestId: ToTrainerRequestId
) extends ParticipantEvent

final case class ParticipantRequestToConvertToTrainerApprovedEvent(
    id: ToTrainerRequestId,
    participantId: ParticipantId,
    name: ParticipantName,
    password: ParticipantPassword,
    preferences: ParticipantPreferences
) extends ParticipantEvent

final case class ParticipantRequestToConvertToTrainerRejectedEvent(
    id: ToTrainerRequestId
) extends ParticipantEvent
