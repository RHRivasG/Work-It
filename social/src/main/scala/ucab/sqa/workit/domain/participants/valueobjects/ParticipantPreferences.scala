package ucab.sqa.workit.domain.participants.valueobjects

case class Preference private[participants] (tag: String)

object Preference {
  private[participants] def apply(tag: String) =
    new Preference(tag)

  def of(tag: String) =
    if (tag.trim == "")
      Left(new Error("Tag name cannot be empty"))
    else
      Right(Preference(tag))

  def unsafeOf(tag: String) = Preference(tag)
}

case class ParticipantPreferences private[participants] (
    preferences: List[Preference]
) {
  def ++(participantPreferences: ParticipantPreferences) =
    ParticipantPreferences(preferences ++ participantPreferences.preferences)

  def --(participantPreferences: ParticipantPreferences) =
    ParticipantPreferences((Set(preferences:_*) -- Set(participantPreferences.preferences:_*)).toList)
}

object ParticipantPreferences {
  private[participants] def apply(preferences: List[Preference]) =
    new ParticipantPreferences(preferences)

  def of(rawPreferences: List[String]): Either[Error, ParticipantPreferences] =
    rawPreferences
      .map { Preference.of(_) }
      .foldLeft(
        Right(
          List.empty[Preference]
        ).asInstanceOf[Either[Error, List[Preference]]]
      ) {
        for (
          accumResult <- _;
          appendResult <- _
        ) yield (accumResult ++ List(appendResult))
      }
      .map { new ParticipantPreferences(_) }

  def unsafeOf(rawPreferences: List[String]) = new ParticipantPreferences(
    rawPreferences map Preference.unsafeOf
  )
}
