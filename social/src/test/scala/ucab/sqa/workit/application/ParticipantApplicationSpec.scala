package ucab.sqa.workit.application

import cats.syntax.all._
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
import ucab.sqa.workit.domain.participants.ParticipantUpdatedEvent
import ucab.sqa.workit.domain.participants.ParticipantPreferencesAdded
import ucab.sqa.workit.domain.participants.ParticipantPreferencesRemoved
import ucab.sqa.workit.domain.participants.ParticipantPasswordChangedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerIssuedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerApprovedEvent
import ucab.sqa.workit.domain.participants.ParticipantRequestToConvertToTrainerRejectedEvent
import ucab.sqa.workit.application.participants.GetAllPreferences
import ucab.sqa.workit.application.participants.PreferenceModel
import ucab.sqa.workit.domain.participants.valueobjects.Preference
import ucab.sqa.workit.application.participants.GetParticipantWithUsername
import ucab.sqa.workit.application.participants.GetAllPreferencesQuery
import ucab.sqa.workit.application.participants.GetParticipantWithUsernameQuery
import ucab.sqa.workit.application.participants.UpdateParticipantCommand
import ucab.sqa.workit.application.participants.IssueRequestParticipantToTrainerCommand

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
      case GetAllPreferences() => mockParticipants
          .values
          .flatMap[Preference](_.preferences.preferences)
          .toList
          .asRight
      case GetParticipantWithUsername(username) => 
        mockParticipants
        .find(_._2.name.name == username)
        .toRight(new Error("Participant with username not found"))
        .map(_._2)
      case GetParticipant(id) =>
        Either.catchNonFatal(mockParticipants(id)).left.map(new Error(_))
      case GetAllParticipants() =>
        Right(mockParticipants.values.toList)
      case Handle(ParticipantCreatedEvent(id, name, password, preferences)) =>
            val participant = Participant(name, password, preferences, id)._2
            mockParticipants.addOne(id.id -> participant)
            Right(())
      case Handle(ParticipantDeletedEvent(id)) =>
            mockParticipants.remove(id.id)
            Right(())
      case Handle(ParticipantUpdatedEvent(id, name)) =>
            mockParticipants.updateWith(id.id) { _.map(part => part.update(name, part.preferences)._2)}
            Right(())
      case Handle(ParticipantPasswordChangedEvent(id, pass)) =>
            mockParticipants.updateWith(id.id) { for {
                part <- _
                (_, newPart) <- part.changePassword(pass).toOption
              } yield newPart 
            }
            Right(())
      case Handle(ParticipantPreferencesAdded(id, preferences)) =>
            mockParticipants.updateWith(id.id) { _.map(part => part.update(part.name, part.preferences ++ preferences)._2)}
            Right(())
      case Handle(ParticipantPreferencesRemoved(id, preferences)) =>
            mockParticipants.updateWith(id.id) { _.map(part => part.update(part.name, part.preferences -- preferences)._2)}
            Right(())
      case Handle(ParticipantRequestToConvertToTrainerIssuedEvent(id, _)) =>
            mockParticipants.updateWith(id.id) { for {
                part <- _
                (_, newPart) <- part.requestToBecomeTrainer.toOption
              } yield newPart 
            }
            Right(())
      case Handle(ParticipantRequestToConvertToTrainerApprovedEvent(id, _, name, password, preferences)) =>
            mockParticipants.updateWith(id.id) { for {
                part <- _
                _ <- part.acceptRequestToBecomeTrainer.toOption
              } yield part 
            }
            Right(())
      case Handle(ParticipantRequestToConvertToTrainerRejectedEvent(id)) =>
            mockParticipants.updateWith(id.id) { for {
                part <- _
                _ <- part.rejectRequestToBecomeTrainer.toOption
              } yield part 
            }
            Right(())
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

  describe("Get all preferences query") {
    it("should return all preferences") {
      val query = GetAllPreferencesQuery()
      val actions = ParticipantApplicationService.query(query)

      val actionResult = actions.run(mockInfrastructure)
      actionResult should matchPattern { case Right(_) => }
    }
  }

  describe("Get participant by username query") {
    it("should return a participant") {
      val query = GetParticipantWithUsernameQuery("Test 2")
      val actions = ParticipantApplicationService.query(query)

      val actionResult = actions.run(mockInfrastructure)
      actionResult should matchPattern { case Right(_) => }
    }

    it("should not return a participant") {
      val query = GetParticipantWithUsernameQuery("Inexistent")
      val actions = ParticipantApplicationService.query(query)

      val actionResult = actions.run(mockInfrastructure)
      actionResult should matchPattern { case Left(_) => }
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

  describe("Update participant command") {
    it("should update a user") {
      val command = UpdateParticipantCommand(participant1.id.id.toString, 
        "First Test", 
        participant1.preferences.preferences.map(_.tag) ++ List("Another preference")
      )
      val actions = ParticipantApplicationService.execute(command)

      val actionResult = actions.run(mockInfrastructure)

      actionResult should matchPattern { case Right(_) => }
      mockParticipants(participant1.id.id).name.name should be("First Test")
      mockParticipants(participant1.id.id).preferences.preferences.map(_.tag) should be(List("Another preference"))
    }
  }

  describe("Issue request participant to trainer command") {
    it("should create a user request") {
      val command = IssueRequestParticipantToTrainerCommand(participant1.id.id.toString)
      val actions = ParticipantApplicationService.execute(command)

      val actionResult = actions.run(mockInfrastructure)

      actionResult should matchPattern { case Right(_) => }
      mockParticipants(participant1.id.id).request.isDefined should be(true)
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
}
