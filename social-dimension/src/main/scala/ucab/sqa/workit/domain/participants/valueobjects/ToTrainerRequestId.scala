package ucab.sqa.workit.domain.participants.valueobjects

import scala.util.Try
import java.util.UUID

case class ToTrainerRequestId private[participants] (id: UUID)

object ToTrainerRequestId {
  private[participants] def apply(id: UUID) = new ToTrainerRequestId(id)

  def of(id: String) =
    Try { UUID.fromString(id) }
      .map(ToTrainerRequestId(_))
      .toEither
      .left
      .map(new Error(_))

  def unsafeOf(id: String) = apply(UUID.fromString(id))
  def unsafeOf(id: UUID) = apply(id)
}
