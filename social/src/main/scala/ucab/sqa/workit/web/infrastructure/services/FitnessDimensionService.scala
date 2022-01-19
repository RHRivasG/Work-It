package ucab.sqa.workit.web.infrastructure.services

import scala.concurrent.ExecutionContext.Implicits.global
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
import akka.grpc.SSLContextUtils
import scala.util.Try
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.netty.handler.ssl.JdkSslContext
import akka.grpc.internal.NettyClientUtils
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider
import akka.actor.FSM
import scala.util.Failure
import scala.util.Success
import akka.Done
import akka.actor.typed.Behavior
import ucab.sqa.workit.probobuf.ParticipantChanged

object FitnessDimensionService {
    sealed trait Command
    final case class DeleteTrainingsOf(trainerID: String) extends Command
    final case class DeleteRoutinesOf(participantId: String) extends Command
    final case class ReassignRoutinesOf(participantId: String) extends Command
    final case class ReassignRoutinesTo(trainerID: String) extends Command

    private def connect[T](service: GrpcClientSettings => T)(implicit system: ActorSystem[_], serviceDiscovery: ServiceDiscovery) = { 
        val grpcClientSettings = 
            GrpcClientSettings
            .usingServiceDiscovery("fitness", serviceDiscovery)
            .withTrustManager(
                SSLContextUtils
                .trustManagerFromStream(
                    getClass.getClassLoader.getResourceAsStream("ca/cert.pem")
                )
            )
        
        service(grpcClientSettings)
    }

    def apply(discovery: ServiceDiscovery) = Behaviors.setup[Command] { ctx =>
        implicit val system = ctx.system
        implicit val serviceDiscovery = discovery

        usualBehavior
    }

    private def usualBehavior(implicit system: ActorSystem[_], serviceDiscovery: ServiceDiscovery): Behavior[Command] = 
        Behaviors.receiveMessagePartial[Command] {
            case DeleteRoutinesOf(participantId) => {
                val service = connect(routineAPIClient(_))
                system.log.info(f"Deleting routines of participant with $participantId")
                (for {
                    _ <- service.deleteByParticipant(ParticipantDeleted(participantId))
                    res <- service.close()
                } yield res)
                .andThen {
                    case Failure(err) => system.log.error(f"Occured error while deleting routines of $participantId", err)
                    case Success(Done) => system.log.info(f"Routines of $participantId deleted")
                }
                Behaviors.same
            }
            case DeleteTrainingsOf(trainerID) => {
                val service = connect(trainingAPIClient(_))
                system.log.info(f"Deleting trainings of trainer with $trainerID")
                (for {
                    _ <- service.deleteByTrainer(TrainerDeleted(trainerID))
                    res <- service.close()
                } yield res)
                .andThen {
                    case Failure(err) => system.log.error(f"Occured error while deleting trainings of $trainerID", err)
                    case Success(Done) => system.log.info(f"Trainings of $trainerID deleted")
                }
                Behaviors.same
            }
            case ReassignRoutinesOf(participantId) => waitForTrainerId(participantId)
        }

    private def waitForTrainerId(participantId: String)(implicit system: ActorSystem[_], serviceDiscovery: ServiceDiscovery): Behavior[Command] = 
        Behaviors.receiveMessagePartial[Command] {
            case ReassignRoutinesTo(trainerID) => 
                val service = connect(routineAPIClient(_))
                system.log.info(f"Reassigning routines of participant with $participantId to $trainerID")
                (for {
                    _ <- service.changeParticipant(ParticipantChanged(participantId, trainerID))
                    res <- service.close()
                } yield res)
                .andThen {
                    case Failure(err) => system.log.error(f"Occured error while reassigning routines", err)
                    case Success(Done) => system.log.info(f"Routines of $participantId reassigned to $trainerID")
                }
                usualBehavior
        }
}