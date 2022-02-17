package ucab.sqa.workit.aggregator

import cats.implicits.*
import cats.effect.IOApp
import cats.effect.IO
import cats.effect.kernel.Resource
import io.grpc.ServerServiceDefinition
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all.*
import io.grpc.protobuf.services.ProtoReflectionService
import io.grpc.ServerInterceptors
import ucab.sqa.workit.aggregator.application.ServiceAggregatorUseCaseBuilder.*
import ucab.sqa.workit.aggregator.infrastructure.ServiceAggregatorExecutor
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import java.io.InputStream
import io.grpc.Metadata

object Main extends IOApp.Simple {
  private val aggregatorService: Resource[IO, ServerServiceDefinition] = for {
    // State resources
    service <- ServiceAggregatorExecutor()
    ipKey <- Resource.pure(Metadata.Key.of("CLIENT_IP", Metadata.ASCII_STRING_MARSHALLER))
    ipInterceptor <- Resource.eval { IO(ClientIpInterceptor(ipKey)) }
    // Server
    server <- ServiceAggregatorFs2Grpc.bindServiceResource[IO](
      ucab.sqa.workit.aggregator.ServiceAggregator(ipKey, service)
    )
  } yield ServerInterceptors.intercept(server, ipInterceptor)

  private val keyFile = Resource.eval {
    for {
      file <- IO { getClass.getClassLoader.getResourceAsStream("aggregator/key.pem") }
    } yield file
  }

  private val certFile = Resource.eval {
    for {
      file <- IO.blocking(getClass.getClassLoader.getResourceAsStream("aggregator/cert.pem")) 
    } yield file
  }

  private val caFile = Resource.eval {
    for {
      file <- IO.blocking(getClass.getClassLoader.getResourceAsStream("ca/cert.pem")) 
    } yield file
  }

  def runServer(service: ServerServiceDefinition, keyFile: InputStream, certFile: InputStream, caFile: InputStream) = NettyServerBuilder
    .forPort(4500)
    .sslContext(
      GrpcSslContexts
      .forServer(certFile, keyFile)
      .trustManager(caFile)
      .build
    )
    .addService(ProtoReflectionService.newInstance())
    .addService(service)
    .resource[IO]
    .evalMap(server => for {
      server <- IO.pure(server.start())
      _ <- IO.println("Started server on port 4500")
    } yield server)
    .useForever

  def run: IO[Unit] = 
    (aggregatorService, keyFile, certFile, caFile)
    .mapN { (service, key, cert, ca) =>
      (service, key, cert, ca)
    }
    .use {
      case (service, key, cert, ca) => runServer(service, key, cert, ca)
    }

}