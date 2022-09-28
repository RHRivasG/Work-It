package ucab.sqa.workit.application.trainers

import ucab.sqa.workit.domain.trainers.Trainer
import ucab.sqa.workit.domain.trainers.TrainerEvent
import cats.free.Free
import cats.data.EitherT
import ucab.sqa.workit.application.ApplicationServiceAction
import cats.Monad
import cats.~>
import java.util.UUID

sealed trait TrainerAction[A]

final case class GetTrainer(id: UUID)
    extends TrainerAction[Either[Error, Trainer]]
final case class GetTrainerWithUsername(name: String)
    extends TrainerAction[Either[Error, Trainer]]
final case class GetTrainers()
    extends TrainerAction[Either[Error, List[Trainer]]]
final case class Handle(evt: TrainerEvent)
    extends TrainerAction[Either[Error, Unit]]

object TrainerActions {
  type TrainerActionF[A] = Free[TrainerAction, A]
  case class TrainerActionEx[A](inner: EitherT[TrainerActionF, Error, A])
      extends ApplicationServiceAction[TrainerAction, A] {

    def flatMap[B](f: A => TrainerActionEx[B]) =
      TrainerActionEx(this.inner.flatMap(f(_).inner))

    def map[B](f: A => B) =
      TrainerActionEx(this.inner.map(f))

    override def run[G[_]](
        executor: TrainerAction ~> G
    )(implicit m: Monad[G]): G[Either[Error, A]] = {
      val naturalK = new (TrainerActionF ~> G) {
        def apply[B](fa: TrainerActionF[B]): G[B] =
          fa.foldMap(executor)
      }

      this.inner.mapK(naturalK).value
    }

  }

  private def liftF[A](action: TrainerAction[A]) = Free.liftF(action)

  def of[A](value: Either[Error, A]): TrainerActionEx[A] =
    TrainerActionEx(EitherT.fromEither(value))

  def getTrainer(id: UUID): TrainerActionEx[Trainer] =
    TrainerActionEx(EitherT(liftF(GetTrainer(id))))

  def getTrainerWithUsername(username: String): TrainerActionEx[Trainer] =
    TrainerActionEx(EitherT(liftF(GetTrainerWithUsername(username))))

  def getAllTrainers: TrainerActionEx[List[Trainer]] =
    TrainerActionEx(EitherT(liftF(GetTrainers())))

  def handle(event: TrainerEvent): TrainerActionEx[Unit] =
    TrainerActionEx(EitherT(liftF(Handle(event))))

  def batch(
      actions: Iterable[TrainerActionEx[Unit]]
  ): TrainerActionEx[Unit] =
    actions reduce {
      for {
        _ <- _
        _ <- _
      } yield ()
    }

}
