package ucab.sqa.workit.web.infrastructure.services

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import java.util.UUID

object AuthDimensionService {
    sealed trait AuthDimensionMessage;
    final case class RegisterParticipant(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class UpdateParticipant(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class UpdateTrainer(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class Unregister(id: UUID) extends AuthDimensionMessage;
    final case class PrepareMoveToTrainer(pid: UUID) extends AuthDimensionMessage;
    final case class CommitMoveToTrainer(tid: UUID) extends AuthDimensionMessage;

    def apply: Behavior[AuthDimensionMessage] =
        Behaviors.receiveMessagePartial[AuthDimensionMessage] {
            case _ => Behaviors.same
        }
}
