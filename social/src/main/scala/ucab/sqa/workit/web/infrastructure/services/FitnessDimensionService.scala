package ucab.sqa.workit.web.infrastructure.services

import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern._
import akka.grpc.GrpcClientSettings
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.discovery.ServiceDiscovery
import ucab.sqa.workit.probobuf.routineAPIClient
import ucab.sqa.workit.probobuf.ParticipantDeleted
import ucab.sqa.workit.probobuf.trainingAPIClient
import ucab.sqa.workit.probobuf.TrainerDeleted

object FitnessDimensionService {
    sealed trait Command
    final case class DeleteTrainingsOf(trainerID: String) extends Command
    final case class DeleteRoutinesOf(participantId: String) extends Command

    private def connect[T](service: GrpcClientSettings => T)(implicit system: ActorSystem[_], serviceDiscovery: ServiceDiscovery) = { 
        val grpcClientSettings = GrpcClientSettings.usingServiceDiscovery("fitness", serviceDiscovery).withTls(false)

        system.log.info(f"Connected to ${grpcClientSettings.serviceName} with port: ${grpcClientSettings.servicePortName}")
        
        service(grpcClientSettings)
    }

    def apply(discovery: ServiceDiscovery) = Behaviors.receive[Command] { (ctx, msg) =>
        implicit val system = ctx.system
        implicit val serviceDiscovery = discovery

        msg match {
            case DeleteRoutinesOf(participantId) => {
                val service = connect(routineAPIClient(_))
                system.log.info(f"Deleting routines of participant with $participantId")
                service.deleteByParticipant(ParticipantDeleted(participantId))
                service.close()
            }
            case DeleteTrainingsOf(trainerID) => {
                val service = connect(trainingAPIClient(_))
                system.log.info(f"Deleting trainings of trainer with $trainerID")
                service.deleteByTrainer(TrainerDeleted(trainerID))
                service.close()
            }
        }
        Behaviors.same
    }
}