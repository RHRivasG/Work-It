package ucab.sqa.workit.web.profile

import cats.syntax.all._
import scala.concurrent.ExecutionContext.Implicits.global
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
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.application.participants.ParticipantModel
import ucab.sqa.workit.web.JsonSupport
import spray.json.JsonFormat
import ucab.sqa.workit.web.Query
import ucab.sqa.workit.application.participants.GetParticipantQuery
import scala.concurrent.Future
import ucab.sqa.workit.application.trainers.GetTrainerQuery
import scala.util.Failure
import scala.util.Success
import cats.data.EitherT
import cats.data.Kleisli

case class AdminProfile(id: String)
case class Profile[A: JsonFormat](`type`: String, user: A)
case class PublicProfile(`type`: String, name: String)

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
                authenticateParticipant(participantService).map(_.asRight[AuthResult[TrainerModel]]) match {
                    case result => 
                        system.log.debug(f"Participant profile lookup ended with $result")
                        result
                }

            def authTrainer =
                authenticateTrainer(trainerService).map(_.asLeft[AuthResult[ParticipantModel]]) match {
                    case result => 
                        system.log.debug(f"Trainer profile lookup ended with $result")
                        result
                }

            def findParticipantOrTrainer(id: String) = {
                val findParticipantV = Kleisli(findParticipant(_))
                val findTrainerV = Kleisli(findTrainer(_))
                val finalResult = findParticipantV orElse findTrainerV

                finalResult.run(id).value
            }
                
    
            concat(
                (path("profile" / Segment.?) & (authParticipant | authTrainer)) { (id, result) =>
                    val effectiveId = id.getOrElse(result.fold(_.fold("admin")(_.id), _.fold("admin")(_.id)))
                    system.log.debug(f"Profile requested with $effectiveId")
                    authorize(result.fold(_.has { _.id == effectiveId }, _.has { _.id == effectiveId })) { 
                        onComplete(findParticipantOrTrainer(effectiveId)) {
                            case Failure(exception) => {
                                system.log.warn(f"Authorization failed with $exception")
                                complete(StatusCodes.Unauthorized, exception.getMessage())
                            }
                            case Success(Left(_)) if effectiveId == "admin" => {
                                complete(AdminProfile("admin"))
                            }
                            case Success(Left(e)) => {
                                system.log.warn(f"Authorization failed with $e")
                                complete(StatusCodes.Unauthorized, e.getMessage())
                            }
                            case Success(Right(Right(trainer))) => {
                                system.log.debug(f"Authorizing trainer profile $trainer")
                                complete(Profile("trainer", trainer))
                            }
                            case Success(Right(Left(participant))) => {
                                system.log.debug(f"Authorizing participant profile $participant")
                                complete(Profile("participant", participant))
                            }
                        } 
                    }
                },
                (pathPrefix("profile") & path("public" / Segment.?) & (authParticipant | authTrainer)) { (id, result) =>
                    val effectiveId = id.getOrElse(result.fold(_.fold("admin")(_.id), _.fold("admin")(_.id)))
                    system.log.debug(f"Public profile requested with $effectiveId")
                    onComplete(findParticipantOrTrainer(effectiveId)) {
                        case Failure(exception) => {
                            system.log.warn(f"Authorization failed with $exception")
                            complete(StatusCodes.Unauthorized, exception.getMessage())
                        }
                        case Success(Left(e)) => {
                            system.log.warn(f"Authorization failed with $e")
                            complete(StatusCodes.Unauthorized, e.getMessage())
                        }
                        case Success(Right(Right(trainer))) => {
                            system.log.debug(f"Authorizing trainer profile $trainer")
                            complete(PublicProfile("trainer", trainer.name))
                        }
                        case Success(Right(Left(participant))) => {
                            system.log.debug(f"Authorizing participant profile $participant")
                            complete(PublicProfile("participant", participant.name))
                        }
                    } 
                }
            )
        }

}