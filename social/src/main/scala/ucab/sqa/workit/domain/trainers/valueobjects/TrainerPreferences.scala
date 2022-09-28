package ucab.sqa.workit.domain.trainers.valueobjects

case class Preference private[trainers] (tag: String)

object Preference {
  private[trainers] def apply(tag: String) =
    new Preference(tag)

  def of(tag: String) =
    if (tag.trim == "")
      Left(new Error("Tag name cannot be empty"))
    else
      Right(Preference(tag))

  def unsafeOf(tag: String) = Preference(tag)
}

case class TrainerPreferences private[trainers] (
    preferences: List[Preference]
)

object TrainerPreferences {
  private[trainers] def apply(preferences: List[Preference]) =
    new TrainerPreferences(preferences)

  def of(rawPreferences: List[String]): Either[Error, TrainerPreferences] =
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
      .map { new TrainerPreferences(_) }

  def unsafeOf(rawPreferences: List[String]) = new TrainerPreferences(
    rawPreferences map Preference.unsafeOf
  )
}
