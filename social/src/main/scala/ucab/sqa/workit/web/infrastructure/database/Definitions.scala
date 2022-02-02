package ucab.sqa.workit.web.infrastructure.database

import java.util.UUID
import slick.ast.ColumnOption
import slick.jdbc.PostgresProfile.api._

private[database] object Definitions {

  class Participants(tag: Tag)
      extends Table[(String, String, UUID)](tag, "participants") {
    def name = column[String]("name")
    def password = column[String]("password")
    def id = column[UUID]("id", O.PrimaryKey)
    def * = (
      name,
      password,
      id
    )
  }

  val participants = TableQuery[Participants]

  class ToTrainerRequests(tag: Tag)
      extends Table[(UUID, UUID)](tag, "to_trainer_requests") {
    def id = column[UUID]("id", O.PrimaryKey)
    def participantId = column[UUID]("participant_id")
    def * = (id, participantId)
  }

  val toTrainerRequests = TableQuery[ToTrainerRequests]

  class Trainers(tag: Tag)
      extends Table[(UUID, String, String)](tag, "trainers") {

    def id = column[UUID]("id", ColumnOption.PrimaryKey)
    def name = column[String]("name", ColumnOption.PrimaryKey)
    def password = column[String]("password", ColumnOption.PrimaryKey)

    override def * = (id, name, password)
  }

  val trainers = TableQuery[Trainers]

  class Preferences(tag: Tag)
      extends Table[(Int, String, Option[UUID], Option[UUID])](
        tag,
        "preferences"
      ) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def value = column[String]("value")
    def participantId = column[Option[UUID]]("participant_id")
    def trainerId = column[Option[UUID]]("trainer_id")
    def * = (
      id,
      value,
      participantId,
      trainerId
    )
  }

  val preferences = TableQuery[Preferences]

  def groupWithPreferences[A](
      seq: Seq[
        (
            A,
            Option[String]
        )
      ]
  ) =
    seq
      .groupBy(_._1)
      .map { (tupleResult) =>
        (
          (
            tupleResult._1,
            tupleResult._2.flatMap {
              _._2.map { List(_) }.getOrElse(List())
            }.toList
          )
        )
      }
}
