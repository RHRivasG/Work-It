package ucab.sqa.workit.web.infrastructure.services

import scala.concurrent.ExecutionContext.Implicits.global
import ucab.sqa.workit.web.helpers
import akka.discovery.ServiceDiscovery
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import java.util.UUID
import ucab.sqa.workit.protobuf.Authenticator
import akka.grpc.SSLContextUtils
import akka.grpc.GrpcClientSettings
import ucab.sqa.workit.protobuf.AuthenticatorClient
import ucab.sqa.workit.protobuf.UserInformation
import akka.actor.typed.Behavior
import ucab.sqa.workit.protobuf.UserId
import ucab.sqa.workit.protobuf.CompleteUserInformation
import ucab.sqa.workit.protobuf.UserRole
import ucab.sqa.workit.protobuf.ChangeRole
import scala.concurrent.Future
import scala.util.Failure

object AuthDimensionService {
    sealed trait AuthDimensionMessage;
    final case class RegisterParticipant(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class UpdateParticipant(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class UpdateTrainer(id: UUID, name: String, password: String, preferences: Array[String]) extends AuthDimensionMessage;
    final case class Unregister(id: UUID) extends AuthDimensionMessage;
    final case class PrepareMoveToTrainer(pid: UUID) extends AuthDimensionMessage;
    final case class CommitMoveToTrainer(tid: UUID) extends AuthDimensionMessage;

    private def connect(serviceDiscovery: ServiceDiscovery)(implicit system: ActorSystem[_]) = { 
        val grpcClientSettings = 
            GrpcClientSettings
            .usingServiceDiscovery("auth", serviceDiscovery)
            .withTrustManager(
                SSLContextUtils
                .trustManagerFromStream(
                    getClass.getClassLoader.getResourceAsStream("ca/cert.pem")
                )
            )
        
        AuthenticatorClient(grpcClientSettings)    
    }
    def apply(discovery: ServiceDiscovery)(implicit actorSystem: ActorSystem[_]): Behavior[AuthDimensionMessage] = {
        Behaviors.receiveMessagePartial[AuthDimensionMessage] {
            case RegisterParticipant(id, name, password, preferences) => {
                (for {
                    service <- Future { connect(discovery) }
                    _ <- Future { println(s"Registering participant with $id, $name, $password, $preferences") }
                    _ <- service.registerParticipant(UserInformation(id.toString, name, password, preferences))
                    _ <- Future { println(s"Registered participant with $id, $name, $password, $preferences") }
                    res <- service.close()
                } yield res).andThen {
                    case Failure(e) => actorSystem.log.error(s"Received error: $e")
                }
                Behaviors.same
            }
            case PrepareMoveToTrainer(pid) => 
                Behaviors.receiveMessagePartial[AuthDimensionMessage] {
                    case CommitMoveToTrainer(tid) => {
                        val service = connect(discovery)
                        (for {
                            _ <- service.setRoleToTrainer(ChangeRole(Some(UserId(pid.toString)), Some(UserId(tid.toString))))
                            res <- service.close()
                        } yield res)

                        AuthDimensionService(discovery)
                    }
                }
            case Unregister(id) =>  {
                val service = connect(discovery)
                (for {
                    _ <- service.unregisterUser(UserId(id.toString))
                    res <- service.close()
                } yield res)
                Behaviors.same
            }
            case UpdateParticipant(id, name, password, preferences) =>  {
                val service = connect(discovery)
                (for {
                    _ <- service.updateUser(CompleteUserInformation(Some(UserInformation(id.toString, name, password, preferences.toSeq)), Some(UserRole("participant"))))
                    res <- service.close()
                } yield res)
                Behaviors.same
            }
            case UpdateTrainer(id, name, password, preferences) =>  {
                val service = connect(discovery)
                (for {
                    _ <- service.updateUser(CompleteUserInformation(Some(UserInformation(id.toString, name, password, preferences.toSeq)), Some(UserRole("trainer"))))
                    res <- service.close()
                } yield res)
                Behaviors.same
            }
        }
    }
}
