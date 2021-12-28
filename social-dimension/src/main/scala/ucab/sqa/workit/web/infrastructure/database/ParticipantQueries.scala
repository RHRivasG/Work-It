package ucab.sqa.workit.web.infrastructure.database
import Definitions._
import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import ucab.sqa.workit.domain.participants.Participant
import scala.concurrent.ExecutionContext.Implicits.global
import cats.syntax._
import cats.implicits._
import cats.Applicative
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import scala.concurrent.Future
import slick.lifted.CanBeQueryCondition

object ParticipantQueries {

  private def toParticipant(
      tuple: (
          String,
          String,
          UUID,
          Option[(UUID, UUID)],
          List[String]
      )
  ) =
    Participant.unsafeOf(
      tuple._1,
      tuple._2,
      tuple._5,
      tuple._4.map { _._1.toString },
      tuple._3.toString
    )

  def groupParticipantWithPreferences(
      seq: Seq[
        (
            ((String, String, UUID), Option[(UUID, UUID)]),
            Option[String]
        )
      ]
  ) =
    groupWithPreferences(seq).map { (t) =>
      toParticipant(
        (
          t._1._1._1,
          t._1._1._2,
          t._1._1._3,
          t._1._2,
          t._2
        )
      )
    }.toList

  def groupParticipantWithPreferencesSingleResult(
      seq: Seq[
        (
            ((String, String, UUID), Option[(UUID, UUID)]),
            Option[String]
        )
      ]
  ) =
    groupWithPreferences(seq)
      .map { (t) =>
        toParticipant(
          (
            t._1._1._1,
            t._1._1._2,
            t._1._1._3,
            t._1._2,
            t._2
          )
        )
      }
      .headOption
      .toRight(new Error("Participant not found with UUID given"))

  private def findParticipantWithCondition[T <: Rep[_]](
      f: Definitions.Participants => T
  )(implicit c: CanBeQueryCondition[T]) = for {
    raw <- participants
      .filter(f)
      .joinLeft(toTrainerRequests)
      .on(_.id === _.participantId)
      .joinLeft(preferences)
      .on(_._1.id === _.participantId)
      .map(t => (t._1, t._2.map(_.value)))
      .result
  } yield groupParticipantWithPreferencesSingleResult(raw)

  def findParticipant(id: UUID) = findParticipantWithCondition(_.id === id)

  def findParticipant(username: String) = findParticipantWithCondition(
    _.name === username
  )

  def getAllParticipants = for {
    raw <- participants
      .joinLeft(toTrainerRequests)
      .on(_.id === _.participantId)
      .joinLeft(preferences)
      .on(_._1.id === _.participantId)
      .map(t => (t._1, t._2.map(_.value)))
      .result
  } yield groupParticipantWithPreferences(raw)

  def getAllPreferences = for {
    raw <- preferences.map(_.value).distinct.result
    prefs <- DBIO.from(Future(for {
      pref <- raw
    } yield Preference.unsafeOf(pref)))
  } yield prefs.toList

  def insertParticipant(
      id: UUID,
      name: String,
      password: String,
      prefs: List[String]
  ) = (
    for {
      _ <- participants += ((name, password, id))
      _ <- preferences.map(c => (c.value, c.participantId)) ++= prefs.map {
        (_, Some(id))
      }.toSeq
    } yield ()
  ).transactionally

  def updateParticipant(
      id: UUID,
      name: String
  ) = for {
    _ <- participants.filter(_.id === id).map(_.name).update(name)
  } yield ()

  def changeParticipantPassword(
      id: UUID,
      password: String
  ) = for {
    _ <- participants.filter(_.id === id).map(_.password).update(password)
  } yield ()

  def deleteParticipant(id: UUID) = for {
    _ <- participants.filter(_.id === id).delete
  } yield ()

  def addParticipantPreferences(
      id: UUID,
      prefs: List[String]
  ) = for {
    prefs <- DBIO.from(Future(prefs.map { (_, Some(id)) }))
    _ <- preferences.map(t => (t.value, t.participantId)) ++= prefs.toSeq
  } yield ()

  def removeParticipantPreferences(
      id: UUID,
      prefs: List[String]
  ) = for {
    _ <- preferences
      .filter(t => t.participantId === id && t.value.inSet(prefs.toSeq))
      .delete
  } yield ()

  def issueParticipantRequest(id: UUID, requestId: UUID) = for {
    _ <- toTrainerRequests += ((requestId, id))
  } yield ()

  def removeParticipantRequest(id: UUID) = for {
    _ <- toTrainerRequests.filter(_.id === id).delete
  } yield ()
}
