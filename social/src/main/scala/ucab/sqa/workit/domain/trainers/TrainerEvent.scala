package ucab.sqa.workit.domain.trainers

import ucab.sqa.workit.domain.trainers.valueobjects.TrainerId
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerName
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPassword
import ucab.sqa.workit.domain.trainers.valueobjects.TrainerPreferences

sealed trait TrainerEvent

final case class TrainerCreatedEvent(
    id: TrainerId,
    name: TrainerName,
    password: TrainerPassword,
    preferences: TrainerPreferences
) extends TrainerEvent

final case class TrainerUpdatedEvent(
    id: TrainerId,
    name: TrainerName
) extends TrainerEvent

final case class TrainerPreferencesAddedEvent(
    id: TrainerId,
    preferences: TrainerPreferences
) extends TrainerEvent

final case class TrainerPreferencesRemovedEvent(
    id: TrainerId,
    preferences: TrainerPreferences
) extends TrainerEvent

final case class TrainerPasswordUpdatedEvent(
    id: TrainerId,
    name: TrainerPassword
) extends TrainerEvent

final case class TrainerDeletedEvent(id: TrainerId) extends TrainerEvent
