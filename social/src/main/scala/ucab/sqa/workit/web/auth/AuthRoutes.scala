package ucab.sqa.workit.web.auth

import cats.syntax.all._
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.application.trainers.TrainerQuery
import ucab.sqa.workit.application.participants.ParticipantQuery
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import ucab.sqa.workit.web.JsonSupport
import scala.concurrent.Future
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.web.Query
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import ucab.sqa.workit.domain.trainers.Trainer
import ucab.sqa.workit.application.trainers.GetTrainerWithUsernameQuery
import scala.concurrent.ExecutionContext.Implicits.global
import ucab.sqa.workit.application.participants.GetParticipantWithUsernameQuery
import ucab.sqa.workit.domain.participants.Participant
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.AttributeKeys
import ucab.sqa.workit.web.helpers
import ucab.sqa.workit.web.helpers.auth.Admin
import ucab.sqa.workit.web.helpers.auth.UserFound
import akka.http.scaladsl.server.Route

class AuthRoutes[C1, C2](
    participantService: ActorRef[Request[C1, ParticipantQuery, _]],
    trainerService: ActorRef[Request[C2, TrainerQuery, _]],
    authorityService: ActorRef[AuthorityActions]
)(implicit system: ActorSystem[_])
    extends JsonSupport {

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("work-it-app.routes.ask-timeout")
  )

  private def issueJWT(subject: String, roles: Seq[String]): Future[String] = 
    authorityService.ask(IssueToken(subject, roles, _))

  private def getIdentityFromToken(credentials: Credentials): Future[Option[helpers.auth.AuthResult[String]]] = credentials match {
    case Provided(token) => authorityService.ask(ValidateToken(token, id => Future.successful(id.asRight), _))
    case _ => Future.successful(None)
  }

  private def getTrainer(username: String): Future[Either[Error, Trainer]] =
    trainerService.ask(
      Query(GetTrainerWithUsernameQuery(username), _)
    )

  private def getParticipant(
      username: String
  ): Future[Either[Error, Participant]] =
    participantService.ask(
      Query(GetParticipantWithUsernameQuery(username), _)
    )

  private def authenticate[T](
      finder: String => Future[Either[Error, T]]
  )(password: T => String)(credentials: Credentials) =
    credentials match {
      case cred @ Provided(username) =>
        for {
          user <- finder(username)
          validUser <- Future(
            for {
              user <- user.toOption
              validUser <-
                (if (cred.verify(password(user))) Some(user)
                 else None)
            } yield validUser
          )
        } yield validUser
      case _ => Future(None)
    }

  val authRoutes = concat(
      pathPrefix("login") {
        concat(
          (path("trainer") & authenticateBasicAsync(
            "Trainer Visible",
            authenticate(getTrainer(_)) { _.password.password }
          )) { trainer =>
            complete(issueJWT(trainer.id.id.toString, Seq("trainer", "participant")))
          },
          (path("participant") & authenticateBasicAsync(
            "Participant Visible",
            authenticate(getParticipant(_)) { _.password.password }
          )) { participant =>
            complete(issueJWT(participant.id.id.toString, Seq("participant")))
          },
          (path("admin") & authorize { request =>
            val ip = request.request.getAttribute(AttributeKeys.remoteAddress)
            ip.flatMap(_.getAddress())
              .filter(addr => addr.isAnyLocalAddress() || addr.isLoopbackAddress())
              .isPresent()
          }) {
            complete(issueJWT("admin", Seq("admin", "participant", "trainer")))
          }
        )
      },
      (path("identity") & authenticateOAuth2Async("Visible", getIdentityFromToken(_))) { id => id match {
          case Admin() => complete("admin")
          case UserFound(token) => complete(token)
        }
      }
    )
}
