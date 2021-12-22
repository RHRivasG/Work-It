package ucab.sqa.workit.application

import org.scalatest.funspec.AnyFunSpec
import org.scalatest._
import flatspec._
import matchers.should._
import ucab.sqa.workit.domain.participants.Participant
import cats._
import scala.util.Try
import ucab.sqa.workit.domain.participants.ParticipantCreatedEvent
import scala.collection.mutable.WeakHashMap
import java.util.UUID
import ucab.sqa.workit.application.participants.ParticipantAction
import ucab.sqa.workit.application.participants.GetParticipant
import ucab.sqa.workit.application.participants.GetAllParticipants
import ucab.sqa.workit.application.participants.Handle
import ucab.sqa.workit.application.participants.CreateParticipantCommand
import ucab.sqa.workit.application.participants.ParticipantApplicationService
import ucab.sqa.workit.application.participants.ParticipantActions
import ucab.sqa.workit.domain.participants.ParticipantDeletedEvent
import ucab.sqa.workit.application.participants.DeleteParticipantCommand
import ucab.sqa.workit.application.participants.GetAllParticipantsQuery

class ParticipantApplicationSpec extends AnyFunSpec with Matchers {
  val participant2 =
    Participant.of("Test 2", "TestPassword", List()).right.get._2
  val participant1 =
    Participant.of("Test 1", "TestPassword", List()).right.get._2

  val mockParticipants = WeakHashMap(
    participant1.id.id -> participant1,
    participant2.id.id -> participant2
  )

  val mockInfrastructure = new (ParticipantAction ~> Id) {
    override def apply[A](fa: ParticipantAction[A]): A = fa match {
      case GetParticipant(id) =>
        Try {
          mockParticipants(id)
        }.toEither.left.map((_) => new Error("Participant not found"))
      case GetAllParticipants() =>
        Right(mockParticipants.values.toList)
      case Handle(evt) =>
        evt match {
          case ParticipantCreatedEvent(id, name, password, preferences) =>
            val participant = Participant(name, password, preferences, id)._2
            mockParticipants.addOne(id.id -> participant)
            Right(())
          case ParticipantDeletedEvent(id) =>
            print(f"Deleting user with id $id")
            mockParticipants.remove(id.id)
            Right(())
        }
    }
  }

  describe("Create participant command") {
    it("should not create a user") {
      val command = CreateParticipantCommand("Test", "Test", List())
      val actions = ParticipantApplicationService.execute(command)

      val actionResult = actions.run(mockInfrastructure)

      actionResult should matchPattern { case Left(_) => }
    }

    it("should create a user") {
      val command = CreateParticipantCommand("Test", "TestPassword", List())
      val actions = ParticipantApplicationService.execute(command)

      val actionResult = actions.run(mockInfrastructure)

      actionResult should matchPattern { case Right(_) => }
      mockParticipants.values.toList.length should be(3)
    }
  }

  describe("Delete participant command") {
    it("should delete a user") {
      val command = DeleteParticipantCommand(participant1.id.id.toString)
      val actions = ParticipantApplicationService.execute(command)

      val actionResult = actions.run(mockInfrastructure)

      actionResult should matchPattern { case Right(_) => }
      mockParticipants.values.toList.length should be(2)
    }
  }

  describe("Get all participants query") {
    it("should return all participants") {
      val query = GetAllParticipantsQuery()
      val actions = ParticipantApplicationService.query(query)

      val actionResult = actions.run(mockInfrastructure)
      actionResult should matchPattern { case Right(_) => }
      actionResult.right.get.length should be(2)

    }
  }
}
