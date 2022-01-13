package ucab.sqa.workit.aggregator

import cats.effect.IO
import cats._
import cats.syntax.all._
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import ucab.sqa.workit.probobuf.aggregator.{RequestServiceMessage, RequestServiceResponse}
import ucab.sqa.workit.probobuf.aggregator.{AddServiceMessage, AddServiceResponse}
import ucab.sqa.workit.probobuf.aggregator.{RemoveServiceMessage, RemoveServiceResponse}
import ucab.sqa.workit.aggregator.application.ServiceAggregatorOrder
import ucab.sqa.workit.aggregator.application.ServiceAggregatorDsl._
import ucab.sqa.workit.aggregator.application.ServiceAggregatorAction
import cats.effect.kernel.Resource
import ucab.sqa.workit.aggregator.model.ServiceTable
import cats.data.EitherT
import cats.data.StateT
import ucab.sqa.workit.aggregator.model.DomainError
import ucab.sqa.workit.aggregator.application.Next
import cats.effect.kernel.Ref
import ucab.sqa.workit.aggregator.application.AddService
import ucab.sqa.workit.aggregator.application.AddServiceCommand
import io.grpc.Metadata
import io.grpc.Grpc
import io.grpc.Context
import ucab.sqa.workit.aggregator.application.RemoveServiceCommand
import cats.effect.std.Semaphore
import ucab.sqa.workit.aggregator.infrastructure.ServiceAggregatorExecutor._

object ServiceAggregator {
    def apply[A](
        ipKey: Metadata.Key[String],
        service: Service,
    ) = new ServiceAggregatorFs2Grpc[IO, Metadata] {
    
        private def executeUseCase[A](order: ServiceAggregatorOrder[A]): IO[A] = for {
            innerResult <- service(order).value
            result <- innerResult.fold(error => IO.raiseError(new Error(f"Occured error $error")), IO.pure)
        } yield result

        override def requestService(request: RequestServiceMessage, ctx: Metadata): IO[RequestServiceResponse] = 
            executeUseCase(Next(request.group))
            .map(service => RequestServiceResponse(service.host.toString))

        override def addService(request: AddServiceMessage, ctx: Metadata): IO[AddServiceResponse] = for {
            ip <- IO.fromEither { Either.catchNonFatal { ctx.get(ipKey) } }
            _ <- executeUseCase(AddServiceCommand(request.group, ip, request.capacity))
        } yield AddServiceResponse(0)

        override def removeService(request: RemoveServiceMessage, ctx: Metadata): IO[RemoveServiceResponse] =
            executeUseCase(RemoveServiceCommand(request.group, request.host))
            .as(RemoveServiceResponse(0))
    }

}
