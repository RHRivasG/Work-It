package ucab.sqa.workit.domain.participants.valueobjects

import java.util.UUID
import scala.util.Failure
import scala.util.Success
import scala.util.Try

case class ParticipantId private[participants] (id: UUID = UUID.randomUUID())

object ParticipantId {
  private[participants] def apply(id: UUID) = new ParticipantId(id)

  def random = new ParticipantId()

  def of(id: String): Either[Error, ParticipantId] =
    Try {
      UUID.fromString(id)
    } match {
      case Success(id) => Right(ParticipantId(id))
      case Failure(ex) => Left(new Error(ex))
    }

  def unsafeOf(id: String) = ParticipantId(UUID.fromString(id))
}
