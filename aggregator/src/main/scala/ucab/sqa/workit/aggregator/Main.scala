package ucab.sqa.workit.aggregator

import cats.implicits._
import cats.instances.tuple._
import cats.effect.IOApp
import cats.effect.IO
import cats.effect.kernel.Resource
import io.grpc.ServerServiceDefinition
import ucab.sqa.workit.aggregator.application.ServiceAggregatorOrder
import ucab.sqa.workit.probobuf.aggregator.AggregatorProto
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorGrpc.ServiceAggregator
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorGrpc
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all._
import cats.effect.kernel.Ref
import scala.collection.immutable.HashMap
import io.grpc.protobuf.services.ProtoReflectionService
import io.grpc.ServerInterceptors
import io.grpc.ServerInterceptor
import io.grpc.InternalServerInterceptors
import java.net.SocketAddress
import io.grpc.Context
import io.grpc.Metadata
import mongo4cats.client.MongoClient
import ucab.sqa.workit.aggregator.application.ServiceAggregatorUseCaseBuilder._
import ucab.sqa.workit.aggregator.infrastructure.ServiceAggregatorExecutor
import ucab.sqa.workit.aggregator.model.ServiceTable
import cats.arrow.FunctionK
import java.net.InetSocketAddress
import io.grpc.netty.shaded.io.netty.channel.ChannelOption
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import java.io.File
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth
import scala.io.Source
import scala.io.BufferedSource
import java.io.InputStream

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
      () <- IO.println(f"Is key file available? ${file.available()}")
    } yield file
  }

  private val certFile = Resource.eval {
    for {
      file <- IO.blocking(getClass.getClassLoader.getResourceAsStream("aggregator/cert.pem")) 
      () <- IO.println(f"Is cert file available? ${file.available()}")
    } yield file
  }

  private val caFile = Resource.eval {
    for {
      file <- IO.blocking(getClass.getClassLoader.getResourceAsStream("ca/cert.pem")) 
      () <- IO.println(f"Is ca-key file available? ${file.available()}")
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