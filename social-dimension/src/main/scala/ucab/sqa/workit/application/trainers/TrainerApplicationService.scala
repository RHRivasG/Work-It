package ucab.sqa.workit.application.trainers

import ucab.sqa.workit.application.ApplicationService
import ucab.sqa.workit.application.ApplicationServiceAction
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerId
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPreferences
import ucab.sqa.workit.domain.trainers.Trainer
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPassword
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerName
import ucab.sqa.workit.application.trainers.TrainerActions._
import java.util.UUID
import scala.util.Try

object TrainerApplicationService
    extends ApplicationService[TrainerAction, TrainerCommand, TrainerQuery] {
  private def trainerModel(trainer: Trainer) =
    TrainerModel(
      trainer.id.id.toString,
      trainer.name.name,
      trainer.preferences.preferences.map { _.tag }
    )

  private def findTrainerById(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    trainer <- getTrainer(id)
  } yield trainerModel(trainer)

  private def findTrainerWithCredentials(username: String, password: String) =
    for {
      trainer <- getTrainerWithUsername(username)
      () <- of(trainer.password == password)
    } yield trainerModel(trainer)

  private def allTrainers = for {
    trainers <- getAllTrainers
  } yield trainers map trainerModel

  private def createTrainer(
      name: String,
      password: String,
      preferences: List[String]
  ) = for {
    name <- of(TrainerName.of(name))
    password <- of(TrainerPassword.of(password))
    preferences <- of(TrainerPreferences.of(preferences))
    (event, _) = Trainer(name, password, preferences)
    () <- handle(event)
  } yield ()

  private def updateTrainer(
      id: String,
      name: String,
      preferences: List[String]
  ) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    trainer <- getTrainer(id)
    name <- of(TrainerName.of(name))
    preferences <- of(TrainerPreferences.of(preferences))
    (eventList, _) <- of(Right(trainer.update(name, preferences)))
    () <- batch(eventList map handle)
  } yield ()

  private def changeTrainerPassword(
      id: String,
      password: String
  ) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    trainer <- getTrainer(id)
    password <- of(TrainerPassword.of(password))
    (evt, _) <- of(trainer.changePassword(password))
    () <- handle(evt)
  } yield ()

  private def deleteTrainer(id: String) = for {
    id <- of(Try {
      UUID.fromString(id)
    }.toEither.left.map(_ => new Error("Invalid UUID")))
    trainer <- getTrainer(id)
    event <- of(Right(trainer.destroy))
    () <- handle(event)
  } yield ()

  override def execute(
      command: TrainerCommand
  ): ApplicationServiceAction[TrainerAction, Unit] = command match {
    case CreateTrainerCommand(name, password, preferences) =>
      createTrainer(name, password, preferences)
    case UpdateTrainerCommand(id, name, preferences) =>
      updateTrainer(id, name, preferences)
    case ChangePasswordTrainerCommand(id, password) =>
      changeTrainerPassword(id, password)
    case DeleteTrainerCommand(id) =>
      deleteTrainer(id)
  }

  override def query[A](
      query: TrainerQuery[A]
  ): ApplicationServiceAction[TrainerAction, A] = query match {
    case GetTrainerQuery(id) => findTrainerById(id)
    case GetTrainersQuery()  => allTrainers
    case GetTrainerWithUsernameQuery(username) =>
      getTrainerWithUsername(username)
  }

}
