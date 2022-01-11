package ucab.sqa.workit.web.infrastructure.trainers

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import ucab.sqa.workit.web.InfrastructureError
import scala.util.Failure
import akka.event.LoggingAdapter
import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import ucab.sqa.workit.web.infrastructure.database.Request
import ucab.sqa.workit.domain.trainers.Trainer
import ucab.sqa.workit.web.trainers.TrainerStreamMessage
import cats.data.EitherT
import ucab.sqa.workit.web.trainers.ResendTrainers

object Infrastructure {
  private type DatabaseRequest = Request.TrainerDatabaseRequest

  def findTrainer(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(Request.GetTrainer(id, _))

  def findTrainer(username: String)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(Request.GetTrainerWithUsername(username, _))

  def findAllTrainers()(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(Request.GetTrainers(_))

  def createTrainerHandler(
      id: UUID,
      name: String,
      password: String,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.CreateTrainer(id, name, password, preferences, _)))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def updateTrainerHandler(
      id: UUID,
      name: String
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.UpdateTrainer(id, name, _)))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def changePasswordTrainerHandler(
      id: UUID,
      password: String
  )(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(Request.ChangeTrainerPassword(id, password, _))

  def deleteTrainerHandler(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      stream: ActorRef[TrainerStreamMessage],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.DeleteTrainer(id, _)))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def preferencesAddedHandler(
      id: UUID,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.AddTrainerPreferences(id, preferences, _)))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def preferencesRemovedHandler(
      id: UUID,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.RemoveTrainerPreferences(id, preferences, _)))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value
}