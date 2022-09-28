package ucab.sqa.workit.application.trainers

sealed trait TrainerCommand

final case class CreateTrainerCommand(
    name: String,
    password: String,
    preferences: List[String]
) extends TrainerCommand

final case class UpdateTrainerCommand(
    id: String,
    name: String,
    preferences: List[String]
) extends TrainerCommand

final case class ChangePasswordTrainerCommand(
    id: String,
    password: String
) extends TrainerCommand

final case class DeleteTrainerCommand(
    id: String
) extends TrainerCommand
