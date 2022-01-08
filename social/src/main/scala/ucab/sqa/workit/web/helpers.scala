package ucab.sqa.workit.web

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Future
import spray.json.JsonFormat
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import _root_.ucab.sqa.workit.web.participants.ParticipantRoutes
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Rejection
import akka.http.scaladsl.server.StandardRoute
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.RejectionHandler
import akka.event.Logging
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.server.Directive
import akka.http.javadsl.server.RejectionHandlerBuilder
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.directives.Credentials
import ucab.sqa.workit.application.participants.ParticipantModel
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.auth.AuthorityActions
import ucab.sqa.workit.web.auth.ValidateToken
import ucab.sqa.workit.application.participants.ParticipantCommand
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.participants.GetParticipantQuery
import akka.util.Timeout
import ucab.sqa.workit.application.trainers.TrainerCommand
import ucab.sqa.workit.application.trainers.TrainerQuery
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.application.trainers.GetTrainerQuery

object helpers {
  object routes {
    case class InfrastructureRejection(e: Throwable) extends Rejection {}

    private case class DomainRejection(e: Throwable) extends Rejection {}

    val rejectDomainErrorAsBadRequest: Directive[Unit] = {
      val domainHandler =
        RejectionHandler.newBuilder.handle { case DomainRejection(e) =>
          complete(StatusCodes.BadRequest, e.getMessage)
        }.result

      handleRejections(domainHandler)
    }

    val rejectDomainErrorAsNotFound: Directive[Unit] = {
      val domainHandler =
        RejectionHandler.newBuilder.handle { case DomainRejection(e) =>
          complete(StatusCodes.NotFound, e.getMessage)
        }.result

      handleRejections(domainHandler)
    }

    def handleFuture[R](
        future: => Future[Either[Error, R]]
    )(implicit
        marshaller: ToEntityMarshaller[R]
    ) =
      onSuccess(future) {
        case Left(e: InfrastructureError) =>
          reject(InfrastructureRejection(e.getCause))
        case Left(e: Error) => reject(DomainRejection(e))
        case Right(e)       => complete(e)
      }

    def handleFuture[R](
        future: => Future[Either[Error, R]],
        msg: String
    ) =
      onSuccess(future) {
        case Left(e: InfrastructureError) =>
          reject(InfrastructureRejection(e.getCause))
        case Left(e: Error) => reject(DomainRejection(e))
        case Right(_)       => complete(msg)
      }
  }

  object auth {

    def delegateAuthenticationEntryPoint = {
      optionalHeaderValueByName("X-Requested-With").flatMap { (requested) =>
        if (requested.filter(_ == "XMLHttpRequest").isDefined)
          mapResponseHeaders { _.filterNot(_.name == "WWW-Authenticate") }
        else
          pass
      }
    }

    private def authenticateParticipantWithCredentials(participantService: ActorRef[Request[ParticipantCommand, ParticipantQuery, _]])(cred: Credentials)(
      implicit authorityService: ActorRef[AuthorityActions],
      timeout: Timeout,
      system: ActorSystem[_]
    ): Future[Option[helpers.auth.AuthResult[ParticipantModel]]] =
      cred match {
        case Credentials.Provided(token) => authorityService.ask(
          ValidateToken(
            token, 
            id => participantService.ask((ref: ActorRef[Either[Error, ParticipantModel]]) => Query(GetParticipantQuery(id), ref)), 
          _)
        )
        case _ => Future(None)
      }

  private def authenticateTrainerWithCredentials(trainerService: ActorRef[Request[TrainerCommand, TrainerQuery, _]])(cred: Credentials)(
    implicit authorityService: ActorRef[AuthorityActions],
    timeout: Timeout,
    system: ActorSystem[_]
  ): Future[Option[helpers.auth.AuthResult[TrainerModel]]] =
    cred match {
      case Credentials.Provided(token) => 
        authorityService.ask(
        ValidateToken(
          token, 
            id => trainerService.ask((ref: ActorRef[Either[Error, TrainerModel]]) => Query(GetTrainerQuery(id), ref)), 
          _)
        )
      case _ => Future(None)
    }

  def authenticateParticipant(participantService: ActorRef[Request[ParticipantCommand, ParticipantQuery, _]])(
      implicit authorityService: ActorRef[AuthorityActions],
      timeout: Timeout,
      system: ActorSystem[_]
    ) =
    authenticateOAuth2Async(
      "Participant Visible",
      authenticateParticipantWithCredentials(participantService)
    )

  def authenticateTrainer(trainerService: ActorRef[Request[TrainerCommand, TrainerQuery, _]])(
    implicit authorityService: ActorRef[AuthorityActions],
    timeout: Timeout,
    system: ActorSystem[_]
  ) =
    authenticateOAuth2Async(
      "Participant Visible",
      authenticateTrainerWithCredentials(trainerService)
    )

    sealed trait AuthResult[T] {
      def has(f: PartialFunction[T, Boolean]): Boolean
    }

    final case class UserFound[T](user: T) extends AuthResult[T] {
      def has(f: PartialFunction[T, Boolean]) =
        f(user)
    }

    final case class Admin[T]() extends AuthResult[T] {
      def has(f: PartialFunction[T, Boolean]) = true
    }

    def user[T](participant: T): AuthResult[T] = UserFound(participant)

    def admin[T]: AuthResult[T] = Admin[T]()

  }
}
