package ucab.sqa.workit.aggregator

import cats.effect.IO
import cats.syntax.all._
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import ucab.sqa.workit.probobuf.aggregator.{RequestServiceMessage, RequestServiceResponse}
import ucab.sqa.workit.probobuf.aggregator.{AddServiceMessage, AddServiceResponse}
import ucab.sqa.workit.aggregator.application.ServiceAggregatorOrder
import ucab.sqa.workit.aggregator.application.Next
import ucab.sqa.workit.aggregator.application.AddServiceCommand
import ucab.sqa.workit.aggregator.application.Unsubscribe
import io.grpc.Metadata
import ucab.sqa.workit.aggregator.infrastructure.ServiceAggregatorExecutor._
import ucab.sqa.workit.probobuf.aggregator.{UnsubscribeMessage, UnsubscribeResponse}

object ServiceAggregator {
    def apply(
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

        override def unsubscribe(request: UnsubscribeMessage, ctx: Metadata): IO[UnsubscribeResponse] = for {
            _ <- IO.println("Unsubsribing service")
            ip <- IO.fromEither { Either.catchNonFatal { ctx.get(ipKey) } }
            _ <- executeUseCase(Unsubscribe(ip))
        } yield UnsubscribeResponse(0)
    }

}
