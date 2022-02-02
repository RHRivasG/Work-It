package ucab.sqa.workit.reports.http

import pureconfig.generic.auto._
import cats.implicits._
import ucab.sqa.workit.reports.infrastructure.fitness.grpc._
import ucab.sqa.workit.reports.infrastructure.notifications.queue._
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication._
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication
import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import cats.effect.kernel.Resource
import cats.effect.kernel.Async
import fs2.Stream
import ucab.sqa.workit.reports.infrastructure._
import ucab.sqa.workit.reports.application._
import ucab.sqa.workit.reports.infrastructure.http.services.ReportService
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.HttpApp
import scala.concurrent.duration.Duration
import pureconfig.ConfigSource
import org.http4s.server.middleware.CORS
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationHandler
import ucab.sqa.workit.reports.infrastructure.fitness.Service

object Main extends IOApp {
  type InfrastructureEffect[A] = ReportIO[IO, A]

  def server[F[_]: Async](routerBuilder: WebSocketBuilder2[F] => HttpApp[F]) = {
    for {
      code <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"3500")
          .withIdleTimeout(Duration.Inf)
          .withHttpWebSocketApp(routerBuilder)
          .build >>
        Resource.eval(Async[F].never)
      )
    } yield code
  }.drain
  def run(args: List[String]) = {
    // Define program
    val program = for {
      // Services
      notificationHandler <- Stream.resource(NotificationHandler.resource[InfrastructureEffect])
      fitnessService <- Stream.resource(Service.resource[InfrastructureEffect])
      service <- Stream.resource(interpreter(notificationHandler)(fitnessService))
      config <- Stream.fromEither[InfrastructureEffect](ConfigSource.default.load[authentication.Configuration] match {
        case Left(v) => Left(new Exception(f"Invalid configuration, missing JWT Key ${v.prettyPrint(2)}"))
        case Right(value) => Right(value)
      })

      // Processes
      cors = CORS
            .policy
            .withAllowCredentials(true)
            .withAllowOriginHeader(_ => true)
      serverStream = server[InfrastructureEffect](builder => cors(Router(
        "/reports" -> ReportService.service[InfrastructureEffect](service, admin(config), participantOrTrainer(config)),
        "/ws" -> ReportService.websocket(service, notificationHandler, builder)
      ).orNotFound)).drain

      // Spawn Processes
      () <- Stream(serverStream, notificationHandler.processingStream).parJoinUnbounded
    } yield ()

    // Run Program
    program.compile.drain.as(ExitCode.Success).value.map { _ match {
        case Left(_) => ExitCode.Error
        case Right(value) => value
      }
    }
  }
}
