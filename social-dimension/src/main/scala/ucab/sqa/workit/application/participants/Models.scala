package ucab.sqa.workit.application.participants

import java.util.UUID

case class ParticipantModel(id: String, name: String, preferences: List[String])

case class PreferenceModel(tag: String)
