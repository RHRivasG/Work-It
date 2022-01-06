package ucab.sqa.workit.web.profile

import cats.syntax.all._
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import akka.http.scaladsl.server.Directives._
import ucab.sqa.workit.application.trainers.TrainerQuery
import ucab.sqa.workit.application.participants.ParticipantCommand
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.trainers.TrainerCommand
import akka.http.scaladsl.model.StatusCodes
import ucab.sqa.workit.web.helpers.auth._
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import ucab.sqa.workit.web.auth.AuthorityActions
import akka.http.scaladsl.server.directives.AuthenticationDirective
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.application.participants.ParticipantModel
import ucab.sqa.workit.web.JsonSupport
import spray.json.JsObject
import spray.json.JsonFormat

case class Profile[A: JsonFormat](`type`: String, user: A)

object ProfileRoutes extends JsonSupport {
    def apply(
        participantService: ActorRef[Request[ParticipantCommand, ParticipantQuery, _]],
        trainerService: ActorRef[Request[TrainerCommand, TrainerQuery, _]])
        (
            implicit system: ActorSystem[_],
            authorityService: ActorRef[AuthorityActions]
        ) = 
        {
            implicit val timeout = Timeout.create(
                system.settings.config.getDuration("work-it-app.routes.ask-timeout")
            )

            def authParticipant =
                authenticateParticipant(participantService).map(_.asRight[AuthResult[TrainerModel]])

            def authTrainer =
                authenticateTrainer(trainerService).map(_.asLeft[AuthResult[ParticipantModel]])
    
            (path("profile" / Segment) & (authParticipant | authTrainer)) { (id, result) =>
                authorize(result.fold(_.has { _.id == id }, _.has { _.id == id })) { 
                    result match {
                        case Left(UserFound(value)) => complete(Profile("trainer", value))
                        case Right(UserFound(value)) => complete(Profile("participant", value))
                        case _ => complete(StatusCodes.NotFound)
                    }
                }
            }
        }

}