package ucab.sqa.workit.web.infrastructure.database

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.PostgresProfile.backend.DatabaseDef
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import ucab.sqa.workit.web.InfrastructureError
import ParticipantQueries._
import TrainerQueries._
import Request._

object DatabaseActor {
  private sealed trait Response

  private final case class QueryResponse[R](
      action: DBIOAction[R, NoStream, Nothing],
      replyTo: ActorRef[Either[Error, R]]
  ) extends Response

  private final case class QueryFalibleResponse[R](
      action: DBIOAction[Either[Error, R], NoStream, Nothing],
      replyTo: ActorRef[Either[Error, R]]
  ) extends Response

  private def replyWithQueryResult(f: => Response)(implicit db: DatabaseDef) =
    f match {
      case QueryResponse(action, replyTo) =>
        replyAfterFuture(replyTo) {
          execute(action)
        }
      case QueryFalibleResponse(action, replyTo) =>
        replyAfterFuture(replyTo) {
          executeFalible(action)
        }
    }

  private def replyAfterFuture[R](replyTo: ActorRef[Either[Error, R]])(
      f: => Future[Either[Error, R]]
  ) = {
    f andThen {
      case Success(e) => replyTo ! e
      case Failure(e) => replyTo ! Left(new InfrastructureError(e))
    }
  }

  private def execute[R](actions: => DBIOAction[R, NoStream, Nothing])(implicit
      db: DatabaseDef
  ) = {
    db
      .run(actions)
      .map(Right(_))
      .recover { e => Left(new InfrastructureError(e)) }
  }

  private def executeFalible[E, R](
      actions: => DBIOAction[Either[Error, R], NoStream, Nothing]
  )(implicit
      db: DatabaseDef
  ) =
    db
      .run(actions)
      .recover { e => Left(new InfrastructureError(e)) }

  def apply(configSection: String) = Behaviors.setup[DatabaseRequest] { _ =>
    implicit val db = Database.forConfig(configSection)

    Behaviors.receiveMessage[DatabaseRequest] { msg =>
      replyWithQueryResult {
        msg match {
          case GetParticipant(id, replyTo) =>
            QueryFalibleResponse(findParticipant(id), replyTo)
          case GetParticipantWithUsername(username, replyTo) =>
            QueryFalibleResponse(findParticipant(username), replyTo)
          case GetParticipants(replyTo) =>
            QueryResponse(getAllParticipants, replyTo)
          case GetPreferences(replyTo) =>
            QueryResponse(getAllPreferences, replyTo)
          case CreateParticipant(id, name, password, prefs, replyTo) =>
            QueryResponse(insertParticipant(id, name, password, prefs), replyTo)
          case UpdateParticipant(id, name, replyTo) =>
            QueryResponse(updateParticipant(id, name), replyTo)
          case ChangeParticipantPassword(id, password, replyTo) =>
            QueryResponse(changeParticipantPassword(id, password), replyTo)
          case DeleteParticipant(id, replyTo) =>
            QueryResponse(deleteParticipant(id), replyTo)
          case AddParticipantPreferences(id, prefs, replyTo) =>
            QueryResponse(addParticipantPreferences(id, prefs), replyTo)
          case RemoveParticipantPreferences(id, prefs, replyTo) =>
            QueryResponse(removeParticipantPreferences(id, prefs), replyTo)
          case IssueParticipantRequest(id, requestId, replyTo) =>
            QueryResponse(issueParticipantRequest(id, requestId), replyTo)
          case AcceptParticipantRequest(id, replyTo) =>
            QueryResponse(removeParticipantRequest(id), replyTo)
          case RejectParticipantRequest(id, replyTo) =>
            QueryResponse(removeParticipantRequest(id), replyTo)
          case GetTrainer(id, replyTo) =>
            QueryFalibleResponse(findTrainer(id), replyTo)
          case GetTrainerWithUsername(username, replyTo) =>
            QueryFalibleResponse(findTrainer(username), replyTo)
          case GetTrainers(replyTo) =>
            QueryResponse(getAllTrainers, replyTo)
          case CreateTrainer(id, name, password, prefs, replyTo) =>
            QueryResponse(insertTrainer(id, name, password, prefs), replyTo)
          case UpdateTrainer(id, name, replyTo) =>
            QueryResponse(updateTrainer(id, name), replyTo)
          case ChangeTrainerPassword(id, password, replyTo) =>
            QueryResponse(changeTrainerPassword(id, password), replyTo)
          case DeleteTrainer(id, replyTo) =>
            QueryResponse(deleteTrainer(id), replyTo)
          case AddTrainerPreferences(id, prefs, replyTo) =>
            QueryResponse(addTrainerPreferences(id, prefs), replyTo)
          case RemoveTrainerPreferences(id, prefs, replyTo) =>
            QueryResponse(removeTrainerPreferences(id, prefs), replyTo)
        }
      }
      Behaviors.same
    }
  }
}
