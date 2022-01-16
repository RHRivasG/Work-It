package ucab.sqa.workit.web.infrastructure.services

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.typed.scaladsl.Behaviors
import ucab.sqa.workit.probobuf.ServiceAggregatorClient
import akka.grpc.GrpcClientSettings
import ucab.sqa.workit.probobuf.AddServiceMessage
import akka.actor.typed.ActorRef
import ucab.sqa.workit.probobuf.RequestServiceMessage
import akka.actor.typed.scaladsl.adapter._
import akka.pattern.{ pipe }
import akka.discovery.ServiceDiscovery
import java.time.Duration
import java.util.concurrent.CompletionStage
import akka.discovery.Lookup
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import ucab.sqa.workit.probobuf.RequestServiceResponse
import java.net.URI
import akka.actor.ExtendedActorSystem
import scala.util.Failure
import scala.util.Success

class ServiceAggregatorDiscovery(system: ExtendedActorSystem) extends ServiceDiscovery {
    implicit val actorSystem = system
    val serviceAggregatorHost = system.settings.config.getString("work-it-app.services.aggregator.host")
    val serviceAggregatorPort = system.settings.config.getInt("work-it-app.services.aggregator.port")
    val serviceAggregator = ServiceAggregatorClient(
        GrpcClientSettings
        .connectToServiceAt(serviceAggregatorHost, serviceAggregatorPort)
        .withTls(false)
    )
    serviceAggregator.addService(AddServiceMessage(group = "social")).andThen {
        case Failure(exception) =>
            system.log.error("Error occured while registering, {}", exception)
            system.terminate()
        case Success(value) => 
            system.log.info("Successfully registered to Service Aggregator")
    }

    sealed trait Message
    case class RequestService(group: String, replyTo: ActorRef[String]) extends Message

    override def lookup(lookup: Lookup, resolveTimeout: FiniteDuration): Future[ServiceDiscovery.Resolved] =
        serviceAggregator
        .requestService(RequestServiceMessage(lookup.serviceName))
        .map {
            case RequestServiceResponse(host, _) => {
                val uri = new URI(host)
                ServiceDiscovery.Resolved(lookup.serviceName, Seq(ServiceDiscovery.ResolvedTarget(uri.getHost(), Some(uri.getPort()), None)))
            }
        }

}
