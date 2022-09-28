package ucab.sqa.workit.reports.infrastructure.publisher.grpc

import cats.~>
import cats.implicits.*
import cats.syntax.all.*
import cats.effect.implicits.*
import cats.effect.kernel.Async
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherAction
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherEvent
import ucab.sqa.workit.reports.infrastructure.publisher.grpc.Configuration
import io.grpc.Metadata
import ucab.sqa.workit.probobuf.aggregator.RequestServiceMessage
import ucab.sqa.workit.probobuf.fitness.trainingAPIFs2Grpc
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.net.URI
import fs2.grpc.syntax.all.*
import ucab.sqa.workit.probobuf.fitness.TrainingDeleted

class GrpcPublisher[F[_]: Async](serviceAgg: ServiceAggregatorFs2Grpc[F, Metadata]) extends (PublisherAction ~> F):
    def apply[A](action: PublisherAction[A]) = action match
        case PublisherAction.Publish(PublisherEvent.ReportAcceptedEvent(trainingId)) => for {
            response <- serviceAgg.requestService(RequestServiceMessage("fitness"), Metadata())
            uri = URI.create(response.host)
            response <- (for 
                channel <- NettyChannelBuilder.forAddress(uri.getHost, uri.getPort).resource[F]
                client <- trainingAPIFs2Grpc.stubResource(channel)
            yield client).use { client => client.delete(TrainingDeleted(trainingId.toString), Metadata()) }
        } yield ()
    
object GrpcPublisher:
    def apply[F[_]: Async] = for
      serviceAggConfiguration <- Configuration[F]
      serviceAggChannel <- NettyChannelBuilder.forAddress(serviceAggConfiguration.host, serviceAggConfiguration.port).resource[F]
      serviceAgg <- ServiceAggregatorFs2Grpc.stubResource(serviceAggChannel)
      publisherInterpreter = new GrpcPublisher[F](serviceAgg)
    yield publisherInterpreter