package ucab.sqa.workit.application.trainers

import ucab.sqa.workit.domain.trainers.Trainer

sealed trait TrainerQuery[A]

final case class GetTrainerQuery(id: String) extends TrainerQuery[TrainerModel]
final case class GetTrainersQuery() extends TrainerQuery[List[TrainerModel]]
final case class GetTrainerWithUsernameQuery(
    username: String
) extends TrainerQuery[Trainer]
