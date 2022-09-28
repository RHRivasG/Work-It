package ucab.sqa.workit.application.participants

import cats.implicits._
import ucab.sqa.workit.application.participants.ParticipantActions._
import ucab.sqa.workit.domain.participants.Participant
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantName
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPreferences
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPassword
import ucab.sqa.workit.application.ApplicationService
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import scala.util.Try
import java.util.UUID

object ParticipantApplicationService
    extends ApplicationService[
      ParticipantAction,
      ParticipantCommand,
      ParticipantQuery
    ] {

  private def participantToModel(participant: Participant) =
    ParticipantModel(
      participant.id.id.toString,
      participant.name.name,
      participant.preferences.preferences.map { _.tag }
    )

  private def preferencesToModel(preference: Preference) =
    PreferenceModel(preference.tag)

  private def findParticipantById(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    participant <- getParticipant(id)
  } yield participantToModel(participant)

  private def allParticipants() = for {
    participants <- getAllParticipants
  } yield participants map participantToModel

  private def allPreferences = for {
    preferences <- getAllPreferences
  } yield preferences map preferencesToModel

  private def getParticipantWithRequestIssued(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    participant <- getParticipant(id)
    result <- of(
      if (participant.request.isEmpty) Left(new Error("No request found"))
      else
        Right(participantToModel(participant))
    )
  } yield result

  private def deleteParticipant(id: String) =
    for {
      id <- of(Try {
        UUID.fromString(id)
      }.toEither.left.map(_ => new Error("Invalid UUID")))
      participant <- getParticipant(id)
      event <- of(Right(participant.destroy))
      _ <- handle(event)
    } yield ()

  private def createParticipant(
      name: String,
      password: String,
      preferences: List[String]
  ) =
    for {
      participants <- getAllParticipants
      (event, p) <- of(Participant.of(name, password, preferences))
      _ <- of(participants.find(_.name == p.name)
            .as(new Error("There already exists a participant with that name"))
            .toLeft(()))
      () <- handle(event)
    } yield ()

  private def changeParticipantPassword(id: String, password: String) =
    for {
      id <- of(Try {
        UUID.fromString(id)
      }.toEither.left.map(_ => new Error("Invalid UUID")))
      participant <- getParticipant(id)
      participantPassword <- of(ParticipantPassword.of(password))
      (event, _) <- of(participant.changePassword(participantPassword))
      _ <- handle(event)
    } yield ()

  private def updateParticipant(
      id: String,
      name: String,
      preferences: List[String]
  ) =
    for {
      id <- of(Try {
        UUID.fromString(id)
      }.toEither.left.map(_ => new Error("Invalid UUID")))
      participant <- getParticipant(id)
      name <- of(ParticipantName.of(name))
      preferences <- of(ParticipantPreferences.of(preferences))
      (eventList, _) <- of(Right(participant.update(name, preferences)))
      _ <- batch(eventList map handle)
    } yield ()

  private def issueRequestParticipantToTrainer(id: String) = for {
    id <-
      of(Try {
        UUID.fromString(id)
      }.toEither.left.map(_ => new Error("Invalid UUID")))
    participant <- getParticipant(id)
    (event, trainer) <- of(participant.requestToBecomeTrainer())
    () <- handle(event)
  } yield ()

  private def acceptRequestParticipantToTrainer(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    participant <- getParticipant(id)
    event <- of(participant.acceptRequestToBecomeTrainer())
    () <- handle(event)
  } yield ()

  private def rejectRequestParticipantToTrainer(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    participant <- getParticipant(id)
    event <- of(participant.rejectRequestToBecomeTrainer())
    () <- handle(event)
  } yield ()

  def execute(command: ParticipantCommand) = command match {
    case CreateParticipantCommand(name, password, preferences) =>
      createParticipant(name, password, preferences)
    case UpdateParticipantCommand(id, name, preferences) =>
      updateParticipant(id, name, preferences)
    case DeleteParticipantCommand(id) =>
      deleteParticipant(id)
    case ChangeParticipantPasswordCommand(id, password) =>
      changeParticipantPassword(id, password)
    case IssueRequestParticipantToTrainerCommand(id) =>
      issueRequestParticipantToTrainer(id)
    case AcceptRequestParticipantToTrainerCommand(id) =>
      acceptRequestParticipantToTrainer(id)
    case RejectRequestParticipantToTrainerCommand(id) =>
      rejectRequestParticipantToTrainer(id)
  }

  def query[A](query: ParticipantQuery[A]) = query match {
    case GetParticipantQuery(id) => findParticipantById(id)
    case GetParticipantWithUsernameQuery(username) =>
      getParticipantWithUsername(username)
    case GetAllParticipantsQuery() => allParticipants()
    case GetAllPreferencesQuery()  => allPreferences
    case GetParticipantWithRequestIssuedQuery(id) =>
      getParticipantWithRequestIssued(id)
  }
}
