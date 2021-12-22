package ucab.sqa.workit.web.participants

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import cats.data.EitherT
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import akka.http.javadsl.model.HttpResponse
import ucab.sqa.workit.web.helpers.routes._
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.web.Query
import ucab.sqa.workit.web.Command
import ucab.sqa.workit.application.participants.ParticipantCommand
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.participants.GetParticipantWithRequestIssuedQuery
import ucab.sqa.workit.application.participants.IssueRequestParticipantToTrainerCommand
import ucab.sqa.workit.application.participants.AcceptRequestParticipantToTrainerCommand
import ucab.sqa.workit.application.participants.RejectRequestParticipantToTrainerCommand
import ucab.sqa.workit.application.participants.GetAllParticipantsQuery
import ucab.sqa.workit.application.participants.GetParticipantQuery
import ucab.sqa.workit.application.participants.CreateParticipantCommand
import ucab.sqa.workit.application.participants.UpdateParticipantCommand
import ucab.sqa.workit.application.participants.DeleteParticipantCommand
import ucab.sqa.workit.application.participants.GetAllPreferencesQuery
import ucab.sqa.workit.application.participants.ParticipantModel
import ucab.sqa.workit.application.participants.PreferenceModel
import ucab.sqa.workit.web.JsonSupport
import ucab.sqa.workit.application.participants.ChangeParticipantPasswordCommand

class ParticipantRoutes(
    participantService: ActorRef[
      Request[ParticipantCommand, ParticipantQuery, _]
    ]
)(implicit val system: ActorSystem[_])
    extends JsonSupport {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("work-it-app.routes.ask-timeout")
  )

  private def getParticipants: Future[Either[Error, List[ParticipantModel]]] =
    participantService.ask(Query(GetAllParticipantsQuery(), _))
  private def getPreferences: Future[Either[Error, List[PreferenceModel]]] =
    participantService.ask(Query(GetAllPreferencesQuery(), _))
  private def getParticipant(
      id: String
  ): Future[Either[Error, ParticipantModel]] =
    participantService.ask(Query(GetParticipantQuery(id), _))
  private def getParticipantWithRequest(
      id: String
  ): Future[Either[Error, ParticipantModel]] =
    participantService.ask(Query(GetParticipantWithRequestIssuedQuery(id), _))
  private def createParticipant(command: CreateParticipantCommand) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(command, rp)
    )
  private def updateParticipant(
      id: String,
      name: String,
      preferences: List[String]
  ) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(
        UpdateParticipantCommand(id, name, preferences),
        rp
      )
    )
  private def changeParticipantPassword(
      id: String,
      password: String
  ) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(
        ChangeParticipantPasswordCommand(id, password),
        rp
      )
    )
  private def deleteParticipant(command: DeleteParticipantCommand) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(command, rp)
    )
  private def issueRequest(id: String) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(IssueRequestParticipantToTrainerCommand(id), rp)
    )
  private def acceptRequest(id: String) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(AcceptRequestParticipantToTrainerCommand(id), rp)
    )
  private def rejectRequest(id: String) =
    participantService.ask((rp: ActorRef[Either[Error, Unit]]) =>
      Command(RejectRequestParticipantToTrainerCommand(id), rp)
    )

  val participantRoutes: Route =
    pathPrefix("participants") {
      concat(
        (pathEnd & rejectDomainErrorAsBadRequest) {
          concat(
            get(handleFuture(getParticipants)),
            (post & entity(as[CreateParticipantCommand])) { command =>
              handleFuture(
                createParticipant(command),
                "Participant was created"
              )
            }
          )
        },
        (path("preferences") & get & rejectDomainErrorAsBadRequest) {
          handleFuture(EitherT(getPreferences).map(_.map { _.tag }).value)
        },
        pathPrefix(Segment) { id =>
          concat(
            pathEnd {
              concat(
                (get & rejectDomainErrorAsNotFound) {
                  handleFuture(getParticipant(id))
                },
                rejectDomainErrorAsBadRequest {
                  concat(
                    (put & entity(as[PartialUpdateParticipantCommand])) {
                      command =>
                        handleFuture(
                          updateParticipant(
                            id,
                            command.name,
                            command.preferences
                          ),
                          "Participant updated"
                        )
                    },
                    delete {
                      handleFuture(
                        deleteParticipant(DeleteParticipantCommand(id)),
                        "Participant deleted"
                      )
                    }
                  )
                }
              )
            },
            pathPrefix("request") {
              concat(
                pathEnd {
                  concat(
                    (get & rejectDomainErrorAsNotFound) {
                      handleFuture(getParticipantWithRequest(id))
                    },
                    (post & rejectDomainErrorAsBadRequest) {
                      handleFuture(issueRequest(id), "Request issued!")
                    }
                  )
                },
                (post & rejectDomainErrorAsBadRequest) {
                  concat(
                    path("accept") {
                      handleFuture(acceptRequest(id), "Request accepted!")
                    },
                    path("reject") {
                      post {
                        handleFuture(rejectRequest(id), "Request rejected!")
                      }
                    }
                  )
                }
              )
            },
            (path("password") & put & rejectDomainErrorAsBadRequest & entity(
              as[PartialChangePasswordParticipantCommand]
            )) { command =>
              handleFuture(
                changeParticipantPassword(id, command.password),
                "Participant updated"
              )
            }
          )
        }
      )
    }
}
