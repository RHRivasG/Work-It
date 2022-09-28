package ucab.sqa.workit.application.participants

import ucab.sqa.workit.domain.participants.Participant

sealed trait ParticipantQuery[A]

final case class GetParticipantQuery(id: String)
    extends ParticipantQuery[ParticipantModel]
final case class GetParticipantWithUsernameQuery(username: String)
    extends ParticipantQuery[Participant]
final case class GetAllParticipantsQuery()
    extends ParticipantQuery[List[ParticipantModel]]
final case class GetAllPreferencesQuery()
    extends ParticipantQuery[List[PreferenceModel]]
final case class GetParticipantWithRequestIssuedQuery(id: String)
    extends ParticipantQuery[ParticipantModel]
