package ucab.sqa.workit.domain.trainers.valueobjects

case class TrainerPassword private[trainers] (password: String) {

  def ==(password: String) =
    if (this.password == password) Right(())
    else Left(new Error("Passwords do not match"))
}

object TrainerPassword {
  private[trainers] def apply(password: String) = new TrainerPassword(
    password
  )

  def of(password: String): Either[Error, TrainerPassword] =
    if (password.trim == "")
      Left(new Error("Password must not be empty"))
    else if (password.length <= 8)
      Left(new Error("Password must have more than 8 characters"))
    else
      Right(TrainerPassword(password))

  def unsafeOf(password: String) = TrainerPassword(password)
}
