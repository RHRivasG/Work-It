package ucab.sqa.workit.web

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

    def optionsPath =
      options {
        complete("API Options")
      }
  }

  object auth {
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
