package ucab.sqa.workit.reports.http

import cats.*
import cats.syntax.all.*
import cats.effect.std.Console
import cats.implicits.*
import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import pureconfig.generic.semiauto
import fs2.Stream
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import cats.effect.IOApp
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import com.typesafe.scalalogging.Logger
import cats.effect.kernel.Resource
import cats.effect.kernel.Async
import scala.concurrent.duration.Duration
import cats.effect.ExitCode
import doobie.util.transactor.Transactor
import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import ucab.sqa.workit.reports.infrastructure.http.services.ReportService
import ucab.sqa.workit.reports.infrastructure.log.`scala-logging`.ScalaLogging
import ucab.sqa.workit.reports.infrastructure.db.lookup.sql.SqlLookup
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import ucab.sqa.workit.reports.infrastructure.notifications.queue.NotificationQueue
import ucab.sqa.workit.reports.infrastructure.publisher.grpc.GrpcPublisher
import ucab.sqa.workit.reports.infrastructure.log.*
import ucab.sqa.workit.reports.infrastructure.execution.effect.EffectExecution
import ucab.sqa.workit.reports.infrastructure.shared.*
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.db.store.sql.SqlStore
import ucab.sqa.workit.reports.infrastructure.db.configuration.Configuration as DatabaseConfiguration
import ucab.sqa.workit.reports.infrastructure.publisher.grpc.Configuration as GrpcConfiguration
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication.Configuration as AuthConfiguration
import org.http4s.server.Router
import pureconfig.ConfigSource
import pureconfig.ConfigReader
import scala.concurrent.ExecutionContextExecutorService
import doobie.util.ExecutionContexts
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import cats.free.FreeApplicative
import cats.effect.std.Queue
import fs2.concurrent.Topic
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import fs2.grpc.syntax.all.*
import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc

object Main extends IOApp {
  import ucab.sqa.workit.reports.application.*

  given ConfigReader[AuthConfiguration] = ConfigReader.forProduct1("secret-key")(AuthConfiguration.apply)

  def factory[F[_]: Async: Console: Parallel] = for
      // Dependencies
      //// Execution Contexts
      qce <- Resource.eval { Async[F].delay { ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(32)) } }
      ece <- Resource.eval { Async[F].delay { ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(32)) } }
      //// Configurations
      dbConfig <- DatabaseConfiguration[F]
      serviceAggConfiguration <- GrpcConfiguration[F]
      //// Database Related
      transactor <- HikariTransactor.newHikariTransactor[F](
        dbConfig.driver,
        dbConfig.url,
        dbConfig.username,
        dbConfig.password,
        qce
      )
      //// Notification Related
      topic <- Resource.eval { Topic[F, Vector[ReportModel]] }
      queue <- Resource.eval { Queue.unbounded[F, Vector[ReportModel]] }
      //// Grpc Related
      serviceAggChannel <- NettyChannelBuilder.forAddress(serviceAggConfiguration.host, serviceAggConfiguration.port).resource[F]
      serviceAgg <- ServiceAggregatorFs2Grpc.stubResource(serviceAggChannel)

      // Interpreters
      lookupInterpreter = SqlLookup[F](transactor)
      storeInterpreter = SqlStore[F](transactor)
      notificationInterpreter = NotificationQueue[F](topic, queue)
      publisherInterpreter = GrpcPublisher[F](serviceAgg)
      logInterpreter = ScalaLogging[F](Logger("AppLogger"))
      //// Composed Interpreter
      composedInterpreter = 
        publisherInterpreter    or (
        notificationInterpreter or (
        logInterpreter          or (
        storeInterpreter        or (
        lookupInterpreter))))
      //// Executor 
      composedExecutor = new (InterpreterLanguage ~> F):
        def apply[A](action: InterpreterLanguage[A]) = 
          action.value.foldMap(EffectExecution(ece, composedInterpreter)).rethrow

  yield (notificationInterpreter.stream, new (ReportAction ~> F):
        def apply[A](action: ReportAction[A]) = action
          .value
          .compile(applicationInterpreter)
          .foldMap(composedExecutor)
          .flatMap {
            case Right(value) => value.pure
            case Left(err) => InfrastructureError.InternalError(err).raiseError
          })

  def server[F[_]: Async: Console](service: ReportAction ~> F, stream: Stream[F, Vector[ReportModel]]) = {
    for
      config <- Stream.eval { 
        ConfigSource.default.at("jwt").load[AuthConfiguration] match
          case Right(config) => config.pure
          case Left(err) => Exception("Bad authentication configuration").raiseError
      }
      code <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"3500")
          .withHttpWebSocketApp(builder => (
            Router(
              "/reports" -> (ReportService.stream(stream, builder) <+> ReportService.service(config, service))
            ).orNotFound
          ))
          .withIdleTimeout(Duration.Inf)
          .build >>
        Resource.eval(Async[F].never)
      )
    yield code
  }.drain

  def run(args: List[String]): InfrastructureResult[ExitCode] =
    // Run Program
    factory[InfrastructureResult].use { service =>
      server[InfrastructureResult].tupled(service.swap).compile.drain.as(ExitCode.Success)
    }
}
