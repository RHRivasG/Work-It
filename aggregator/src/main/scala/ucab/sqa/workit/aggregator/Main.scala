package ucab.sqa.workit.aggregator

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
import cats._
import io.grpc.Metadata
import mongo4cats.client.MongoClient
import ucab.sqa.workit.aggregator.application.ServiceAggregatorUseCaseBuilder._
import ucab.sqa.workit.aggregator.infrastructure.ServiceAggregatorExecutor
import ucab.sqa.workit.aggregator.model.ServiceTable
import cats.arrow.FunctionK
import java.net.InetSocketAddress

object Main extends IOApp.Simple {
  val aggregatorService: Resource[IO, ServerServiceDefinition] = for {
    // State resources
    service <- ServiceAggregatorExecutor()
    ipKey <- Resource.pure(Metadata.Key.of("CLIENT_IP", Metadata.ASCII_STRING_MARSHALLER))
    ipInterceptor <- Resource.eval { IO(ClientIpInterceptor(ipKey)) }
    // Server
    server <- ServiceAggregatorFs2Grpc.bindServiceResource[IO](
      ucab.sqa.workit.aggregator.ServiceAggregator(ipKey, service)
    )
  } yield ServerInterceptors.intercept(server, ipInterceptor)

  def runServer(service: ServerServiceDefinition) = NettyServerBuilder
    .forPort(4500)
    .addService(ProtoReflectionService.newInstance())
    .addService(service)
    .resource[IO]
    .evalMap(server => for {
      server <- IO.pure(server.start())
      _ <- IO.println("Started server on port 4500")
    } yield server)
    .useForever

  def run: IO[Unit] = 
    aggregatorService.use(runServer)

}