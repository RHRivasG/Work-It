package ucab.sqa.workit.reports.http

import cats.*
import cats.syntax.all.*
import cats.effect.std.Console
import cats.implicits.*
import cats.effect.IOApp
import cats.data.Kleisli
import pureconfig.ConfigReader
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.mtl.Tell
import cats.effect.kernel.Async
import ucab.sqa.workit.reports.application.UseCase
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication.Configuration
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import ucab.sqa.workit.reports.infrastructure.InfrastructureError 
import ucab.sqa.workit.reports.infrastructure.InfrastructureCompiler
import ucab.sqa.workit.reports.infrastructure.db.SqlDatabase
import ucab.sqa.workit.reports.infrastructure.notifications.queue.NotificationQueue
import ucab.sqa.workit.reports.infrastructure.publisher.grpc.GrpcPublisher
import ucab.sqa.workit.reports.infrastructure.log.`scala-logging`.ScalaLogging
import ucab.sqa.workit.reports.infrastructure.execution.effect.EffectExecution
import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import ucab.sqa.workit.reports.infrastructure.http.services.ReportService
import ucab.sqa.workit.reports.infrastructure.transaction.*
import cats.effect.kernel.Sync
import cats.effect.ExitCode
import pureconfig.ConfigSource
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import scala.concurrent.duration.Duration
import cats.effect.kernel.Resource

type StreamedUseCase[F[_]] = UseCase[[A] =>> fs2.Stream[F, A], F]
type InfrastructureResult[A] = Kleisli[InfrastructureEffect, Ref[InfrastructureEffect, InfrastructureEffect[Unit]], A]
type InfrastructureEffect[A] = IO[A]

object Main extends IOApp {

  given ConfigReader[Configuration] = ConfigReader.forProduct1("secret-key")(Configuration.apply)

  def factory[F[_]: Async: Parallel: [F[_]] =>> Tell[F, F[Unit]]] = for
      // Build Interpreters
      (lookupInterpreter, storeInterpreter) <- SqlDatabase[F]
      notificationInterpreter <- NotificationQueue[F]
      publisherInterpreter <- GrpcPublisher[F]
      logInterpreter = ScalaLogging[F]
      // Composed Interpreter
      composedInterpreter = 
        publisherInterpreter    or (
        notificationInterpreter or (
        logInterpreter          or (
        storeInterpreter        or (
        lookupInterpreter))))
      // Complete Interpreter
      finalInterpreter <- EffectExecution.apply <*> composedInterpreter.pure
      // Interpreter
      interpreter = new (InfrastructureLanguage ~> F):
        def apply[A](f: InfrastructureLanguage[A]) = f
          .value
          .foldMap(finalInterpreter)
          .rethrow

  yield (notificationInterpreter.stream, interpreter)

  def server[F[_]: Async: Console: StreamedUseCase] = {
    for
      config <- fs2.Stream.eval { 
        ConfigSource.default.at("jwt").load[Configuration] match
          case Right(config) => config.pure
          case Left(err) => Exception("Bad authentication configuration").raiseError
      }
      code <- fs2.Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"3500")
          .withHttpWebSocketApp(builder => (
            Router(
              "/reports" -> (ReportService.stream(builder) <+> ReportService.service(config))
            ).orNotFound
          ))
          .withIdleTimeout(Duration.Inf)
          .build >>
        Resource.eval(Async[F].never)
      )
    yield code
  }.drain

  // Run Program
  def run(args: List[String]): InfrastructureEffect[ExitCode] = factory[InfrastructureResult].mapK(Transaction.executeK).use { case (stream, interpreter) =>
    given StreamedUseCase[InfrastructureEffect] = UseCase.build(
      stream.translate(Transaction.executeK), 
      InfrastructureError.InternalError(_), 
      InfrastructureCompiler, 
      interpreter andThen Transaction.executeK
    )
    server[InfrastructureEffect].compile.drain.as(ExitCode.Success)
  }
}
