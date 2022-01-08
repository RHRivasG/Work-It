package ucab.sqa.workit.web.profile

import scala.concurrent.ExecutionContext.Implicits.global
import cats.syntax.all._
import akka.actor.typed.scaladsl.AskPattern._
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
import ucab.sqa.workit.web.Query
import ucab.sqa.workit.application.participants.GetParticipantQuery
import scala.concurrent.Future
import ucab.sqa.workit.application.trainers.GetTrainerQuery
import scala.util.Failure
import scala.util.Success
import cats.data.EitherT
import cats.data.Kleisli

case class Profile[A: JsonFormat](`type`: String, user: A)

object ProfileRoutes extends JsonSupport {
    type FindResult[T] = EitherT[Future, Error, T]

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

            def findParticipant(id: String) = EitherT(
                participantService.ask((ref: ActorRef[Either[Error, ParticipantModel]]) => Query(GetParticipantQuery(id), ref)).map { _ match {
                    case Right(x) => x.asLeft[TrainerModel].asRight[Error]
                    case Left(e) => e.asLeft[Either[ParticipantModel, TrainerModel]]
                }}
            )

            def findTrainer(id: String) = EitherT(
                trainerService.ask((ref: ActorRef[Either[Error, TrainerModel]]) => Query(GetTrainerQuery(id), ref)).map { r => println(r); r match {
                    case Right(x) => x.asRight[ParticipantModel].asRight[Error]
                    case Left(e) => e.asLeft[Either[ParticipantModel, TrainerModel]]
                }}
            )

            def authParticipant =
                authenticateParticipant(participantService).map(_.asRight[AuthResult[TrainerModel]])

            def authTrainer =
                authenticateTrainer(trainerService).map(_.asLeft[AuthResult[ParticipantModel]])

            def findParticipantOrTrainer(id: String) = {
                val findParticipantV = Kleisli(findParticipant(_))
                val findTrainerV = Kleisli(findTrainer(_))
                val finalResult = findParticipantV orElse findTrainerV

                finalResult.run(id).value
            }
                
    
            (path("profile" / Segment) & (authParticipant | authTrainer)) { (id, result) =>
                authorize(result.fold(_.has { _.id == id }, _.has { _.id == id })) { 
                    rejectEmptyResponse {
                        onComplete(findParticipantOrTrainer(id)) {
                            case Failure(exception) => complete(None)
                            case Success(Left(e)) => complete(None)
                            case Success(Right(Right(trainer))) => complete(Profile("trainer", trainer))
                            case Success(Right(Left(participant))) => complete(Profile("participant", participant))
                        } 
                    }
                }
            }
        }

}