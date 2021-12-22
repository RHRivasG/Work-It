package ucab.sqa.workit.domain.trainers.valueobjects

case class TrainerName private[trainers] (name: String)

object TrainerName {
  private[trainers] def apply(name: String) = new TrainerName(name)

  def of(name: String): Either[Error, TrainerName] =
    if (name.trim == "")
      Left(new Error("Participant name empty"))
    else
      Right(TrainerName(name))

  def unsafeOf(name: String) = TrainerName(name)
}
