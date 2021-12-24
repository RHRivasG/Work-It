package ucab.sqa.workit.web.trainers

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.application.trainers.TrainerCommand
import ucab.sqa.workit.application.trainers.TrainerQuery
import akka.http.scaladsl.server.Route
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Directives._
import ucab.sqa.workit.web.helpers.routes._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ucab.sqa.workit.web.Query
import ucab.sqa.workit.application.trainers.GetTrainersQuery
import ucab.sqa.workit.application.trainers.CreateTrainerCommand
import ucab.sqa.workit.web.Command
import ucab.sqa.workit.application.trainers.UpdateTrainerCommand
import ucab.sqa.workit.application.trainers.GetTrainerQuery
import ucab.sqa.workit.application.trainers.ChangePasswordTrainerCommand
import ucab.sqa.workit.application.trainers.DeleteTrainerCommand
import ucab.sqa.workit.web.JsonSupport
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.web.helpers
import cats.data.OptionT
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import akka.http.scaladsl.server.directives.Credentials

class TrainerRoutes(
    trainersService: ActorRef[Request[TrainerCommand, TrainerQuery, _]]
)(implicit system: ActorSystem[_])
    extends JsonSupport {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("work-it-app.routes.ask-timeout")
  )

  private def getAllTrainers: Future[Either[Error, List[TrainerModel]]] =
    trainersService.ask(Query(GetTrainersQuery(), _))
  private def getTrainer(
      id: String
  ): Future[Either[Error, TrainerModel]] =
    trainersService.ask(Query(GetTrainerQuery(id), _))
  private def updateTrainer(
      id: String,
      name: String,
      preferences: List[String]
  ): Future[Either[Error, Unit]] =
    trainersService.ask(Command(UpdateTrainerCommand(id, name, preferences), _))
  private def changeTrainerPassword(
      id: String,
      password: String
  ): Future[Either[Error, Unit]] =
    trainersService.ask(
      Command(ChangePasswordTrainerCommand(id, password), _)
    )
  private def deleteTrainer(id: String): Future[Either[Error, Unit]] =
    trainersService.ask(Command(DeleteTrainerCommand(id), _))

  private def authenticateTrainerWithCredentials(
      cred: Credentials
  ) = (
    cred match {
      case Credentials.Provided(token) =>
        for {
          payload <- OptionT.fromOption[Future](
            JwtCirce.decode(token, "secret", Seq(JwtAlgorithm.HS256)).toOption
          )
          subject <- OptionT.fromOption[Future](payload.subject)
          user <-
            OptionT(getTrainer(subject).map(_.toOption))
              .map[helpers.auth.AuthResult[TrainerModel]](
                helpers.auth.user(_)
              )
              .orElse {
                if (subject == "admin")
                  OptionT.pure[Future](helpers.auth.admin)
                else
                  OptionT
                    .none[Future, helpers.auth.AuthResult[TrainerModel]]
              }
        } yield user
      case _ => OptionT.none[Future, helpers.auth.AuthResult[TrainerModel]]
    }
  ).value

  private def authenticateTrainer =
    authenticateOAuth2Async(
      "Participant Visible",
      authenticateTrainerWithCredentials
    )

  def trainerRoutes: Route =
    pathPrefix("trainers") {
      concat(
        pathEnd {
          rejectDomainErrorAsBadRequest {
            (get & authenticateTrainer) { user =>
              authorize(user.has(_ => false)) {
                handleFuture(getAllTrainers)
              }
            }
          }
        },
        pathPrefix(Segment) { id =>
          concat(
            pathEnd {
              concat(
                (get & rejectDomainErrorAsNotFound & authenticateTrainer) {
                  user =>
                    authorize(user.has(_.id == id))(
                      handleFuture(getTrainer(id))
                    )
                },
                rejectDomainErrorAsBadRequest {
                  concat(
                    (put & authenticateTrainer) { user =>
                      authorize(user.has(_.id == id)) {
                        entity(as[PartialUpdateTrainerCommand]) { command =>
                          handleFuture(
                            updateTrainer(
                              id,
                              command.name,
                              command.preferences
                            ),
                            "Trainer Updated!"
                          )
                        }
                      }
                    },
                    (delete & authenticateTrainer) { user =>
                      authorize(user.has(_.id == id)) {
                        handleFuture(deleteTrainer(id), "Trainer deleted!")
                      }
                    }
                  )
                }
              )
            },
            (path(
              "password"
            ) & put & authenticateTrainer & rejectDomainErrorAsBadRequest & entity(
              as[PartialChangePasswordTraninerCommand]
            )) { (user, command) =>
              authorize(user.has(_.id == id)) {
                handleFuture(
                  changeTrainerPassword(id, command.password),
                  "Trainer Password Changed!"
                )
              }
            }
          )
        }
      )
    }
}
