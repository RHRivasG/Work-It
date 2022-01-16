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
import ucab.sqa.workit.domain.participants.Participant
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import scala.util.Success
import cats.data.EitherT

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
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.CreateParticipant(id, name, password, preferences, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants()) 
    } yield ()
  ).value
    

  def updateHandler(id: UUID, name: String)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.UpdateParticipant(id, name, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants()) 
    } yield ()
  ).value

  def passwordChangedHandler(id: UUID, password: String)(implicit
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = ref.ask(
    database.Request.ChangeParticipantPassword(id, password, _)
  )

  def deleteHandler(id: UUID)(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      fitness: ActorRef[FitnessDimensionService.Command],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.DeleteParticipant(id, _)))
      _ <- EitherT.pure[Future, Error](fitness ! FitnessDimensionService.DeleteRoutinesOf(id.toString))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value

  def preferencesAddedHandler(id: UUID, prefs: List[String])(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.AddParticipantPreferences(id, prefs, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
    } yield ()
  ).value

  def preferencesRemovedHandler(id: UUID, prefs: List[String])(implicit
      ref: ActorRef[DatabaseRequest],
      stream: ActorRef[ParticipantStreamMessage],
      system: ActorSystem[_],
      to: Timeout
  ) = (
    for {
      _ <- EitherT(ref.ask(database.Request.RemoveParticipantPreferences(id, prefs, _)))
      _ <- EitherT.pure[Future, Error](stream ! ResendParticipants())
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
      name: String,
      password: String,
      preferences: List[String]
  )(implicit
      actorRef: ActorRef[
        Request[TrainerCommand, Q, _]
      ],
      stream: ActorRef[ParticipantStreamMessage],
      ref: ActorRef[DatabaseRequest],
      system: ActorSystem[_],
      to: Timeout
  ) = {
    actorRef ! Notification(CreateTrainerCommand(name, password, preferences))
    (
      for {
        _ <- EitherT(ref.ask(database.Request.AcceptParticipantRequest(id, _)))
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
