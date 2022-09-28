package ucab.sqa.workit.application.participants

case class ParticipantModel(id: String, name: String, preferences: List[String])

case class PreferenceModel(tag: String)
