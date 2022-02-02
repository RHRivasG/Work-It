package ucab.sqa.workit.reports.infrastructure.fitness

import cats.implicits._
import fs2.grpc.syntax.all._
import pureconfig.generic.auto._
import cats.effect.kernel.Resource
import pureconfig.ConfigSource
import cats.effect.kernel.Sync
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import cats.effect.kernel.Async
import ucab.sqa.workit.probobuf.aggregator.RequestServiceMessage
import io.grpc.Metadata
import ucab.sqa.workit.probobuf.fitness.trainingAPIFs2Grpc
import java.net.URI
import ucab.sqa.workit.probobuf.fitness.TrainingDeleted
import cats.effect.std
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ApplicationProtocolConfig

package object grpc {
    private def loadCert[F[_]: Sync] = Sync[F].blocking(getClass.getClassLoader.getResourceAsStream("ca/cert.pem"))
    implicit def grpcFitnessCommunication[F[_] : Async : std.Console] = for {
        configResult <- Resource.eval { Sync[F].blocking(ConfigSource.default.load[Configuration]) }
        cert <- Resource.eval { loadCert }
        context = SslContextBuilder
                  .forClient()
                  .trustManager(cert)
                  .applicationProtocolConfig(new ApplicationProtocolConfig(
                    ApplicationProtocolConfig.Protocol.ALPN, 
                    ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
                    "h2"
                  ))
        config <- Resource.eval { configResult match {
            case Left(value) => Sync[F].raiseError(new Exception(value.toString()))
            case Right(value) => Sync[F].pure(value)
        } }
        channel <- NettyChannelBuilder
                   .forAddress(config.aggregator.host, config.aggregator.port)
                   .sslContext(context.build())
                   .resource[F]
        _ <- Resource.eval { std.Console[F].println(f"Connected to aggregator") }
        aggregator <- ServiceAggregatorFs2Grpc.stubResource[F](channel)
        _ <- Resource.eval { std.Console[F].println(f"Created stub") }
    } yield new Service[F] {
      override def deleteTraining(id: String): F[_] = (for {
        _ <- std.Console[F].println(f"Requesting service")
        service <- aggregator.requestService(RequestServiceMessage("fitness"), new Metadata()).onError {
            err => std.Console[F].errorln(err)
        }
        _ <- std.Console[F].println(f"Got service $service")
        uri = URI.create(service.host)
        _ <- NettyChannelBuilder
                   .forAddress(uri.getHost(), uri.getPort())
                   .sslContext(context.build())
                   .resource[F]
                   .flatMap(channel => trainingAPIFs2Grpc.stubResource[F](channel))
                   .use { trainingApiStub => for {
                        _ <- std.Console[F].println(f"Issuing deletion")
                        result <- trainingApiStub.delete(TrainingDeleted(id), new Metadata()) onError {
                            err => std.Console[F].printStackTrace(err)
                        }
                        _ <- std.Console[F].println(f"Got result from deletion: $result")
                    } yield result.msg }
      } yield ()).recover(_ => ())
    }
}