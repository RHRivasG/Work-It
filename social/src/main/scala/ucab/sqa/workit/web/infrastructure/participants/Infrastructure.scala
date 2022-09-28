package ucab.sqa.workit.web.infrastructure.participants

import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.web.Notification
import ucab.sqa.workit.application.trainers.TrainerCommand
import ucab.sqa.workit.application.trainers.CreateTrainerCommand
import ucab.sqa.workit.web.infrastructure.database.Request.ParticipantDatabaseRequest
import ucab.sqa.workit.web.infrastructure.database
import ucab.sqa.workit.web.infrastructure.services.FitnessDimensionService
import ucab.sqa.workit.web.participants.ParticipantStreamMessage
import ucab.sqa.workit.web.participants.ResendParticipants
import akka.actor.typed.scaladsl.AskPattern._
import scala.concurrent.Future
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import cats.data.EitherT
import ucab.sqa.workit.web.infrastructure.services.AuthDimensionService

object Infrastructure {
  private type DatabaseRequest = ParticipantDatabaseRequest

  def findParticipant(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(database.Request.GetParticipant(id, _))

  def findParticipant(username: String)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(database.Request.GetParticipantWithUsername(username, _))

  def findParticipants()(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(database.Request.GetParticipants(_))

  def findPreferences()(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(database.Request.GetPreferences(_))

  def createHandler(
      id: UUID,
      name: String,
      password: String,
      preferences: List[String]
  )(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.CreateParticipant(id, name, password, preferences, _)))
      _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.RegisterParticipant(id, name, password, preferences.toArray))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants()) 
    } yield ()
  ).value
    

  def updateHandler(id: UUID, name: String)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.UpdateParticipant(id, name, _)))
      user <- EitherT(ref.ask(database.Request.GetParticipant(id, _)))
      _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
              user.id.id, 
              user.name.name, 
              user.password.password, 
              user.preferences.preferences.map(_.tag).toArray
      ))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants()) 
    } yield ()
  ).value

  def passwordChangedHandler(id: UUID, password: String)(implicit
      ref: ActorRef[DatabaseRequest],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (for {
    _ <- EitherT(ref.ask(database.Request.ChangeParticipantPassword(id, password, _)))
    user <- EitherT(ref.ask(database.Request.GetParticipant(id, _)))
    _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
          user.id.id, 
          user.name.name, 
          user.password.password, 
          user.preferences.preferences.map(_.tag).toArray
      ))
  } yield ()).value

  def deleteHandler(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      fitness: ActorRef[FitnessDimensionService.Command],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.DeleteParticipant(id, _)))
      _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.Unregister(id))
      _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.DeleteRoutinesOf(id.toString))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value

  def preferencesAddedHandler(id: UUID, prefs: List[String])(implicit
      ref: ActorRef[DatabaseRequest],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.AddParticipantPreferences(id, prefs, _)))
      user <- EitherT(ref.ask(database.Request.GetParticipant(id, _)))
      _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
            user.id.id, 
            user.name.name, 
            user.password.password, 
            user.preferences.preferences.map(_.tag).toArray
        ))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value

  def preferencesRemovedHandler(id: UUID, prefs: List[String])(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.RemoveParticipantPreferences(id, prefs, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
      user <- EitherT(ref.ask(database.Request.GetParticipant(id, _)))
      _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.UpdateParticipant(
            user.id.id, 
            user.name.name, 
            user.password.password, 
            user.preferences.preferences.map(_.tag).toArray
        ))
    } yield ()
  ).value

  def requestIssuedHandler(id: UUID, requestId: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.IssueParticipantRequest(id, requestId, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value

  def requestApprovedHandler[Q[_]](
      id: UUID,
      participantId: UUID,
      name: String,
      password: String,
      preferences: List[String]
  )(implicit
      actorRef: ActorRef[
        Request[TrainerCommand, Q, _]
      ],
      fitness: ActorRef[FitnessDimensionService.Command],
      stream: ActorRef[ParticipantStreamMessage],
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      auth: ActorRef[AuthDimensionService.AuthDimensionMessage],
      to: Timeout
  ) = {
    (
      for {
        _ <- EitherT(ref.ask(database.Request.AcceptParticipantRequest(id, _)))
        _ <- EitherT(ref.ask(database.Request.DeleteParticipant(participantId, _)))
        _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.ReassignRoutinesOf(participantId.toString))
        _ <- EitherT.pure[Future, Error](actorRef ! Notification(CreateTrainerCommand(name, password, preferences)))
        _ <- EitherT.pure[Future, Error](auth ! AuthDimensionService.PrepareMoveToTrainer(participantId))
        _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
      } yield ()
    ).value
  }

  def requestRejectedHandler(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.RejectParticipantRequest(id, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value
}
