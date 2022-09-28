package ucab.sqa.workit.web.infrastructure.services

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior

object FitnessDimensionService {
    sealed trait Command
    final case class DeleteTrainingsOf(trainerID: String) extends Command
    final case class DeleteRoutinesOf(participantId: String) extends Command
    final case class ReassignRoutinesOf(participantId: String) extends Command
    final case class ReassignRoutinesTo(trainerID: String) extends Command


    def apply = Behaviors.setup[Command] { ctx =>
        implicit val system = ctx.system
        usualBehavior
    }

    private def usualBehavior(implicit system: ActorSystem[_]): Behavior[Command] = 
        Behaviors.receiveMessagePartial[Command] {
            case DeleteRoutinesOf(_) => {
                Behaviors.same
            }
            case DeleteTrainingsOf(_) => {
                Behaviors.same
            }
            case ReassignRoutinesOf(participantId) => waitForTrainerId(participantId)
        }

    private def waitForTrainerId(participantId: String)(implicit system: ActorSystem[_]): Behavior[Command] = 
        Behaviors.receiveMessagePartial[Command] {
            case ReassignRoutinesTo(trainerID) => 
                system.log.info(f"Reassigning routines of participant with $participantId to $trainerID")
                usualBehavior
        }
}