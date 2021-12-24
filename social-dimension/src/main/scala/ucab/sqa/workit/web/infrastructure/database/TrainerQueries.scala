package ucab.sqa.workit.web.infrastructure.database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import Definitions._
import java.util.UUID
import ucab.sqa.workit.domain.trainers.Trainer
import scala.concurrent.Future
import slick.lifted.CanBeQueryCondition

object TrainerQueries {
  private def toTrainer(
      tuple: (
          UUID,
          String,
          String,
          List[String]
      )
  ) =
    Trainer.unsafeOf(
      tuple._2,
      tuple._3,
      tuple._4,
      tuple._1.toString
    )

  private def groupTrainerWithPreferencesWithSingleResult(
      seq: Seq[
        (
            (UUID, String, String),
            Option[String]
        )
      ]
  ) =
    groupWithPreferences(seq)
      .map { (tupleResult) =>
        toTrainer(
          (
            tupleResult._1._1,
            tupleResult._1._2,
            tupleResult._1._3,
            tupleResult._2
          )
        )
      }
      .headOption
      .toRight(new Error("Trainer not found with UUID given"))

  private def groupTrianerWithPreferences(
      seq: Seq[
        (
            (UUID, String, String),
            Option[String]
        )
      ]
  ) =
    groupWithPreferences(seq)
      .map { (tupleResult) =>
        toTrainer(
          (
            tupleResult._1._1,
            tupleResult._1._2,
            tupleResult._1._3,
            tupleResult._2
          )
        )
      }

  private def findTrainerWithCondition[T <: Rep[_]](
      f: Definitions.Trainers => T
  )(implicit c: CanBeQueryCondition[T]) = for {
    raw <- trainers
      .filter(f)
      .joinLeft(preferences)
      .on(_.id === _.trainerId)
      .map(t => (t._1, t._2.map(_.value)))
      .result
  } yield groupTrainerWithPreferencesWithSingleResult(raw)

  def findTrainer(id: UUID) = findTrainerWithCondition(_.id === id)

  def findTrainer(username: String) = findTrainerWithCondition(
    _.name === username
  )

  def getAllTrainers = for {
    raw <- trainers
      .joinLeft(preferences)
      .on(_.id === _.trainerId)
      .map(t => (t._1, t._2.map(_.value)))
      .result
  } yield groupTrianerWithPreferences(raw).toList

  def insertTrainer(
      id: UUID,
      name: String,
      password: String,
      prefs: List[String]
  ) = (
    for {
      _ <- trainers += ((id, name, password))
      _ <- preferences.map(c => (c.value, c.trainerId)) ++= prefs.map {
        (_, Some(id))
      }.toSeq
    } yield ()
  ).transactionally

  def updateTrainer(
      id: UUID,
      name: String
  ) = for {
    _ <- trainers.filter(_.id === id).map(_.name).update(name)
  } yield ()

  def changeTrainerPassword(
      id: UUID,
      password: String
  ) = for {
    _ <- trainers.filter(_.id === id).map(_.password).update(password)
  } yield ()

  def deleteTrainer(id: UUID) = for {
    _ <- trainers.filter(_.id === id).delete
  } yield ()

  def addTrainerPreferences(
      id: UUID,
      prefs: List[String]
  ) = for {
    prefs <- DBIO.from(Future(prefs.map { (_, Some(id)) }))
    _ <- preferences.map(t => (t.value, t.trainerId)) ++= prefs.toSeq
  } yield ()

  def removeTrainerPreferences(
      id: UUID,
      prefs: List[String]
  ) = for {
    _ <- preferences
      .filter(t => t.trainerId === id && t.value.inSet(prefs.toSeq))
      .delete
  } yield ()
}
