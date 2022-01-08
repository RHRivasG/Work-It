package ucab.sqa.workit.aggregator

import cats.effect.IOApp
import cats.effect.IO
import cats.effect.kernel.Resource
import io.grpc.ServerServiceDefinition
import ucab.sqa.workit.probobuf.aggregator.AggregatorProto
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorGrpc.ServiceAggregator
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all._
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorGrpc
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc

object Main extends IOApp.Simple {
  val aggregatorService: Resource[IO, ServerServiceDefinition] = 
    // ServiceAggregatorFs2Grpc.bindServiceResource[IO]()
    ???
  def run: IO[Unit] =
    IO.println("Hello World")
}
