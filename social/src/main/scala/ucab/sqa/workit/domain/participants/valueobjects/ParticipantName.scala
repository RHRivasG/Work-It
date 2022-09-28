package ucab.sqa.workit.domain.participants.valueobjects

case class ParticipantName private[participants] (name: String)

object ParticipantName {
  private[participants] def apply(name: String) = new ParticipantName(name)

  def of(name: String): Either[Error, ParticipantName] =
    if (name.trim == "")
      Left(new Error("Participant name empty"))
    else
      Right(ParticipantName(name))

  def unsafeOf(name: String) = ParticipantName(name)
}
