package ucab.sqa.workit.domain.trainers

import ucab.sqa.workit.domain.trainers.valueobjects.TrainerId
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerName
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPassword
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPreferences

case class Trainer private[trainers] (
    id: TrainerId,
    name: TrainerName,
    password: TrainerPassword,
    preferences: TrainerPreferences
) {
  def update(name: TrainerName, preferences: TrainerPreferences) = {
    val currentPreferences = Set(this.preferences.preferences: _*)
    val newPreferences = Set(preferences.preferences: _*)
    val addedPreferences = (newPreferences -- currentPreferences).toList
    val deletedPreferences = (currentPreferences -- newPreferences).toList
    val events =
      List(TrainerUpdatedEvent(this.id, name)) ++
        (if (!addedPreferences.isEmpty)
           List(
             TrainerPreferencesAddedEvent(
               this.id,
               TrainerPreferences(addedPreferences)
             )
           )
         else List()) ++
        (if (!deletedPreferences.isEmpty)
           List(
             TrainerPreferencesRemovedEvent(
               this.id,
               TrainerPreferences(deletedPreferences)
             )
           )
         else
           List())

    (events, this.copy(name = name, preferences = preferences))
  }

  def changePassword(password: TrainerPassword) =
    if (password.password == this.password)
      Left(new Error("Passwords must be different"))
    else
      Right(
        (
          TrainerPasswordUpdatedEvent(id, password),
          this.copy(password = password)
        )
      )

  def destroy() =
    TrainerDeletedEvent(this.id)
}

object Trainer {
  def apply(
      name: TrainerName,
      password: TrainerPassword,
      preferences: TrainerPreferences,
      id: TrainerId = TrainerId.random
  ) = (
    TrainerCreatedEvent(id, name, password, preferences),
    new Trainer(id, name, password, preferences)
  )

  def unsafeOf(
      name: String,
      password: String,
      preferences: List[String],
      id: String
  ) = new Trainer(
    TrainerId.unsafeOf(id),
    TrainerName.unsafeOf(name),
    TrainerPassword.unsafeOf(password),
    TrainerPreferences.unsafeOf(preferences)
  )
}
