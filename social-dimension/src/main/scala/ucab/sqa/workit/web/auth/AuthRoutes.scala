package ucab.sqa.workit.web.auth

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
import java.time.Instant
import pdi.jwt.JwtClaim
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.AttributeKeys

class AuthRoutes[C1, C2](
    participantService: ActorRef[Request[C1, ParticipantQuery, _]],
    trainerService: ActorRef[Request[C2, TrainerQuery, _]]
)(implicit system: ActorSystem[_])
    extends JsonSupport {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("work-it-app.routes.ask-timeout")
  )

  private def issueJWT(subject: String) = {
    val claims = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(1200).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond),
      subject = Some(subject),
      audience = Some(Set("auth", "social", "fitness"))
    )
    val key = "secret"
    val algo = JwtAlgorithm.HS256

    JwtCirce.encode(claims, key, algo)
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

  val authRoutes = pathPrefix("login") {
    concat(
      (path("trainers") & authenticateBasicAsync(
        "Trainer Visible",
        authenticate(getTrainer(_)) { _.password.password }
      )) { trainer =>
        complete(issueJWT(trainer.id.id.toString))
      },
      (path("participants") & authenticateBasicAsync(
        "Participant Visible",
        authenticate(getParticipant(_)) { _.password.password }
      )) { participant =>
        complete(issueJWT(participant.id.id.toString))
      },
      (path("admin") & authorize { request =>
        val ip = request.request.getAttribute(AttributeKeys.remoteAddress)
        ip.flatMap(_.getAddress())
          .filter(addr => addr.isAnyLocalAddress() || addr.isLoopbackAddress())
          .isPresent()
      }) {
        complete(issueJWT("admin"))
      }
    )
  }
}
