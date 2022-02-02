package ucab.sqa.workit.web.infrastructure.services

import cats.implicits._
import akka.actor.typed.scaladsl.AskPattern._
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
import akka.discovery.Lookup
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import ucab.sqa.workit.probobuf.RequestServiceResponse
import java.net.URI
import akka.actor.ExtendedActorSystem
import scala.util.Failure
import scala.util.Success
import ucab.sqa.workit.probobuf.UnsubscribeMessage
import akka.util.Timeout
import akka.actor.CoordinatedShutdown
import akka.Done
import akka.grpc.SSLContextUtils

private object ServiceAggregatorDiscovery {
    sealed trait Request
    final case class RequestService(lookup: Lookup, replyTo: ActorRef[ServiceDiscovery.Resolved]) extends Request
    final case class Stop(replyTo: ActorRef[Done]) extends Request

    def apply() = Behaviors.setup[Request] { ctx =>
        implicit val system = ctx.system
        val serviceAggregatorHost = system.settings.config.getString("work-it-app.services.aggregator.host")
        val serviceAggregatorPort = system.settings.config.getInt("work-it-app.services.aggregator.port")
        val serviceAggregator = ServiceAggregatorClient(
            GrpcClientSettings
            .connectToServiceAt(serviceAggregatorHost, serviceAggregatorPort)
            .withTrustManager(
                SSLContextUtils
                .trustManagerFromStream(
                    getClass.getClassLoader.getResourceAsStream("ca/cert.pem")
                )
            )
        )
        serviceAggregator.addService(AddServiceMessage(group = "social", capacity = 1)).andThen {
            case Failure(exception) =>
                system.log.error("Error occured while registering, {}", exception)
                system.terminate()
            case Success(_) => 
                system.log.info("Successfully registered to Service Aggregator")
        }
        Behaviors.receiveMessagePartial {
            case RequestService(lookup, replyTo) =>
                serviceAggregator
                .requestService(RequestServiceMessage(lookup.serviceName))
                .map {
                    case RequestServiceResponse(host, _) => {
                        val uri = new URI(host)
                        system.log.info(f"Returning ${uri.getHost()}:${uri.getPort()} to query $lookup")
                        ServiceDiscovery.Resolved(lookup.serviceName, Seq(ServiceDiscovery.ResolvedTarget(uri.getHost(), Some(uri.getPort()), None)))
                    }
                }
                .pipeTo(replyTo.toClassic)
                Behaviors.same
            case Stop(replyTo) =>
                (serviceAggregator.unsubscribe(UnsubscribeMessage()), serviceAggregator.close()).mapN { (_, _) =>
                    replyTo ! Done 
                }
                Behaviors.stopped
        }
    }
}

class ServiceAggregatorDiscovery(system: ExtendedActorSystem) extends ServiceDiscovery {
    implicit val actorSystem = system.classicSystem.toTyped
    private val aggregator = actorSystem.systemActorOf(
        ServiceAggregatorDiscovery(),
        "ServiceAggregatorDiscovery",
    )

    CoordinatedShutdown(actorSystem).addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "Unsubscribing from Service Aggregator") { () =>
        implicit val timeout = Timeout.create(Duration.ofSeconds(30))
        aggregator.ask(ServiceAggregatorDiscovery.Stop)     
    }

    override def lookup(lookup: Lookup, resolveTimeout: FiniteDuration): Future[ServiceDiscovery.Resolved] = {
        implicit val timeout = Timeout.durationToTimeout(resolveTimeout)
        aggregator.ask(ServiceAggregatorDiscovery.RequestService(lookup, _))
    }

}
