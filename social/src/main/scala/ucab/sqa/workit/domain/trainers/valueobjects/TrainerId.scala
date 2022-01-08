package ucab.sqa.workit.domain.trainers.valueobjects

import java.util.UUID
import scala.util.Failure
import scala.util.Success
import scala.util.Try

case class TrainerId private[trainers] (id: UUID = UUID.randomUUID())

object TrainerId {
  private[trainers] def apply(id: UUID) = new TrainerId(id)

  def random = new TrainerId()

  def of(id: String): Either[Error, TrainerId] =
    Try {
      UUID.fromString(id)
    } match {
      case Success(id) => Right(TrainerId(id))
      case Failure(ex) => Left(new Error(ex))
    }

  def unsafeOf(id: String) = TrainerId(UUID.fromString(id))
}
