package ucab.sqa.workit.application.participants

import ucab.sqa.workit.domain.participants.Participant
import ucab.sqa.workit.domain.participants.ParticipantEvent
import cats.data.EitherT
import cats._
import ucab.sqa.workit.application.ApplicationServiceAction
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import java.util.UUID

sealed trait ParticipantAction[A]

final case class GetAllParticipants()
    extends ParticipantAction[Either[Error, List[Participant]]]

final case class GetAllPreferences()
    extends ParticipantAction[Either[Error, List[Preference]]]

final case class GetParticipant(id: UUID)
    extends ParticipantAction[Either[Error, Participant]]

final case class GetParticipantWithUsername(username: String)
    extends ParticipantAction[Either[Error, Participant]]

final case class Handle(event: ParticipantEvent)
    extends ParticipantAction[Either[Error, Unit]]

object ParticipantActions {
  import cats.free.Free

  type ParticipantActionF[A] = Free[ParticipantAction, A]

  case class ParticipantActionEx[A](
      inner: EitherT[ParticipantActionF, Error, A]
  ) extends ApplicationServiceAction[ParticipantAction, A] {

    def flatMap[B](f: A => ParticipantActionEx[B]): ParticipantActionEx[B] =
      ParticipantActionEx(
        this.inner.flatMap(f(_).inner)
      )

    def map[B](f: A => B): ParticipantActionEx[B] =
      ParticipantActionEx(this.inner.map(f))

    override def run[G[_]](
        executor: ParticipantAction ~> G
    )(implicit m: Monad[G]): G[Either[Error, A]] = {
      val fullTransformation = new (ParticipantActionF ~> G) {
        override def apply[B](fa: ParticipantActionF[B]): G[B] =
          fa foldMap executor
      }
      this.inner.mapK(fullTransformation).value
    }

  }

  private def liftF[A](action: ParticipantAction[A]) = Free.liftF(action)

  def of[A](value: Either[Error, A]): ParticipantActionEx[A] =
    ParticipantActionEx(EitherT.fromEither(value))

  def getAllPreferences =
    ParticipantActionEx(EitherT(liftF(GetAllPreferences())))

  def getParticipantWithUsername(
      username: String
  ): ParticipantActionEx[Participant] =
    ParticipantActionEx(EitherT(liftF(GetParticipantWithUsername(username))))

  def getParticipant(id: UUID): ParticipantActionEx[Participant] =
    ParticipantActionEx(EitherT(liftF(GetParticipant(id))))

  def getAllParticipants: ParticipantActionEx[List[Participant]] =
    ParticipantActionEx(EitherT(liftF(GetAllParticipants())))

  def handle(event: ParticipantEvent): ParticipantActionEx[Unit] =
    ParticipantActionEx(EitherT(liftF(Handle(event))))

  def batch(
      actions: Iterable[ParticipantActionEx[Unit]]
  ): ParticipantActionEx[Unit] =
    actions reduce {
      for {
        _ <- _
        _ <- _
      } yield ()
    }
}
