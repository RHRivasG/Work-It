package ucab.sqa.workit.web.infrastructure.trainers
import cats._
import scala.concurrent.Future
import ucab.sqa.workit.application.trainers.TrainerAction
import ucab.sqa.workit.application.trainers.GetTrainer
import ucab.sqa.workit.application.trainers.GetTrainers
import ucab.sqa.workit.domain.trainers.TrainerCreatedEvent
import ucab.sqa.workit.application.trainers.Handle
import ucab.sqa.workit.domain.trainers.TrainerUpdatedEvent
import ucab.sqa.workit.domain.trainers.TrainerDeletedEvent
import ucab.sqa.workit.domain.trainers.TrainerPreferencesAddedEvent
import ucab.sqa.workit.domain.trainers.TrainerPreferencesRemovedEvent
import ucab.sqa.workit.domain.trainers.TrainerPasswordUpdatedEvent
import ucab.sqa.workit.domain.trainers.Trainer
import java.util.UUID
import ucab.sqa.workit.application.trainers.GetTrainerWithUsername

case class InfrastructureHandler(
    trainerLookup: UUID => Future[Either[Error, Trainer]],
    trainerLookupByName: String => Future[Either[Error, Trainer]],
    trainersLookup: () => Future[Either[Error, List[Trainer]]],
    creationHandler: (
        UUID,
        String,
        String,
        List[String]
    ) => Future[Either[Error, Unit]],
    updateHandler: (UUID, String) => Future[Either[Error, Unit]],
    passwordChangeHandler: (UUID, String) => Future[Either[Error, Unit]],
    deletionHandler: UUID => Future[Either[Error, Unit]],
    preferencesAddedHandler: (
        UUID,
        List[String]
    ) => Future[Either[Error, Unit]],
    preferencesRemovedHandler: (
        UUID,
        List[String]
    ) => Future[Either[Error, Unit]]
) extends (TrainerAction ~> Future) {

  override def apply[A](fa: TrainerAction[A]): Future[A] = fa match {
    case GetTrainer(id)                   => trainerLookup(id)
    case GetTrainerWithUsername(username) => trainerLookupByName(username)
    case GetTrainers()                    => trainersLookup()
    case Handle(TrainerCreatedEvent(id, name, password, preferences)) =>
      creationHandler(
        id.id,
        name.name,
        password.password,
        preferences.preferences map { _.tag }
      )
    case Handle(TrainerUpdatedEvent(id, name)) =>
      updateHandler(
        id.id,
        name.name
      )
    case Handle(TrainerPasswordUpdatedEvent(id, password)) =>
      passwordChangeHandler(
        id.id,
        password.password
      )
    case Handle(TrainerDeletedEvent(id)) =>
      deletionHandler(
        id.id
      )
    case Handle(TrainerPreferencesAddedEvent(id, preferences)) =>
      preferencesAddedHandler(
        id.id,
        preferences.preferences map { _.tag }
      )
    case Handle(TrainerPreferencesRemovedEvent(id, preferences)) =>
      preferencesRemovedHandler(
        id.id,
        preferences.preferences map { _.tag }
      )
  }

}
