package ucab.sqa.workit.domain.participants.valueobjects

import cats.Applicative
import cats.implicits._

case class ParticipantPassword private[participants] (password: String) {
  def ==(password: String) =
    if (this.password == password) Right(())
    else Left(new Error("Passwords do not match"))
}

object ParticipantPassword {
  private[participants] def apply(password: String) = new ParticipantPassword(
    password
  )

  private def validateLength(password: String) = 
    if (password.trim == "")
      Left(new Error("Password must not be empty"))
    else if (password.length <= 8)
      Left(new Error("Password must have more than 8 characters"))
    else
      Right(password)

  private def validateLowercaseCharacters(password: String) = {
    val regex = "[a-z]+".r
    (regex findFirstIn password).toRight(new Error("Password must contain at least one lowercase character"))
  }

  private def validateUppercaseCharacters(password: String) = {
    val regex = "[A-Z]+".r
    (regex findFirstIn password).toRight(new Error("Password must contain at least one uppercase character"))
  }

  private def validateNumericalCharacters(password: String) = {
    val regex = "[1-9]+".r
    (regex findFirstIn password).toRight(new Error("Password must contain at least one number"))
  }

  private def validateSpecialCharacters(password: String) = {
    val regex = """[-.,;:/\{}\[\]()@&^*%$#!?]++""".r
    (regex findFirstIn password).toRight(new Error("Password must contain at least one special character"))
  }

  def of(password: String): Either[Error, ParticipantPassword] = 
    validateLength(password) >>
    validateLowercaseCharacters(password) >>
    validateUppercaseCharacters(password) >>
    validateNumericalCharacters(password) >>
    validateSpecialCharacters(password) >>
    Right(ParticipantPassword(password))

  def unsafeOf(password: String) = ParticipantPassword(password)
}
