package ucab.sqa.workit.domain.participants.valueobjects

case class ParticipantPassword private[participants] (password: String) {
  def ==(password: String) =
    if (this.password == password) Right(())
    else Left(new Error("Passwords do not match"))
}

object ParticipantPassword {
  private[participants] def apply(password: String) = new ParticipantPassword(
    password
  )

  def of(password: String): Either[Error, ParticipantPassword] =
    if (password.trim == "")
      Left(new Error("Password must not be empty"))
    else if (password.length <= 8)
      Left(new Error("Password must have more than 8 characters"))
    else
      Right(ParticipantPassword(password))

  def unsafeOf(password: String) = ParticipantPassword(password)
}
