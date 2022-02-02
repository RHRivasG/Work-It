package ucab.sqa.workit.web.infrastructure.trainers

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import ucab.sqa.workit.web.infrastructure.database.Request
import ucab.sqa.workit.web.trainers.TrainerStreamMessage
import ucab.sqa.workit.web.infrastructure.services.FitnessDimensionService
import cats.data.EitherT
import ucab.sqa.workit.web.trainers.ResendTrainers
import ucab.sqa.workit.web.infrastructure.services.AuthDimensionService

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
      fitness: ActorRef[FitnessDimensionService.Command],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.CreateTrainer(id, name, password, preferences, _)))
    _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.ReassignRoutinesTo(id.toString))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.CommitMoveToTrainer(id))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def updateTrainerHandler(
      id: UUID,
      name: String
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      system: ActorSystem[_],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.UpdateTrainer(id, name, _)))
    user <- EitherT(ref.ask(Request.GetTrainer(id, _)))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
        user.id.id, 
        user.name.name, 
        user.password.password, 
        user.preferences.preferences.map(_.tag).toArray
    ))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def changePasswordTrainerHandler(
      id: UUID,
      password: String
  )(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      to: Timeout
  ) = (
      for {
        _ <- EitherT(ref.ask(Request.ChangeTrainerPassword(id, password, _)))
        user <- EitherT(ref.ask(Request.GetTrainer(id, _)))
        _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
            user.id.id, 
            user.name.name, 
            user.password.password, 
            user.preferences.preferences.map(_.tag).toArray
        ))
      } yield ()
    ).value

  def deleteTrainerHandler(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      fitness: ActorRef[FitnessDimensionService.Command],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      stream: ActorRef[TrainerStreamMessage],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.DeleteTrainer(id, _)))
    _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.DeleteRoutinesOf(id.toString))
    _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.DeleteTrainingsOf(id.toString))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.Unregister(id))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def preferencesAddedHandler(
      id: UUID,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.AddTrainerPreferences(id, preferences, _)))
    user <- EitherT(ref.ask(Request.GetTrainer(id, _)))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
        user.id.id, 
        user.name.name, 
        user.password.password, 
        user.preferences.preferences.map(_.tag).toArray
    ))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value

  def preferencesRemovedHandler(
      id: UUID,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[TrainerStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(Request.RemoveTrainerPreferences(id, preferences, _)))
    user <- EitherT(ref.ask(Request.GetTrainer(id, _)))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
        user.id.id, 
        user.name.name, 
        user.password.password, 
        user.preferences.preferences.map(_.tag).toArray
    ))
    _ <- EitherT.pure[Future, Error](stream ! ResendTrainers())
  } yield ()).value
}
