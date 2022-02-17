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
import ucab.sqa.workit.reports.infrastructure.db.configuration.*
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import ucab.sqa.workit.reports.infrastructure.log.*
import ucab.sqa.workit.reports.infrastructure.shared.*
import org.http4s.server.Router
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication.Configuration
import pureconfig.ConfigSource
import pureconfig.ConfigReader
import scala.concurrent.ExecutionContextExecutorService
import doobie.util.ExecutionContexts
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import ucab.sqa.workit.reports.infrastructure.db.store.sql.SqlStore
import cats.free.FreeApplicative


object Main extends IOApp {
  import ucab.sqa.workit.reports.application.*

  given ConfigReader[Configuration] = ConfigReader.forProduct1("secret-key")(Configuration.apply)

  def factory[F[_]: Async: Console] = for
      // Dependencies
      ce <- Resource.eval { Async[F].blocking { ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(32)) } }
      dbConfig <- Resource.eval { getConfiguration }
      transactor <- HikariTransactor.newHikariTransactor[F](
        dbConfig.driver,
        dbConfig.url,
        dbConfig.username,
        dbConfig.password,
        ce
      )

      // Interpreters
      lookupInterpreter = SqlLookup[F](transactor)
      storeInterpreter = SqlStore[F](transactor)


      databaseInterpreter = storeInterpreter or lookupInterpreter
      logInterpreter = ScalaLogging[F](Logger("AppLogger"))

      // Composed interpreter
      composedInterpreter = databaseInterpreter

      // Cross cutting concerns interpreter
      aopInterpreter = logInterpreter

      // Final Executor
      composedExecutor = new (InterpreterAction ~> F):
        def apply[A](action: InterpreterAction[A]) = 
          action.foldMap(new (([A] =>> FreeApplicative[InterpreterInstructionAop, A]) ~> F):
            def apply[A](free: FreeApplicative[InterpreterInstructionAop, A]) = 
              free.foldMap(aopInterpreter or composedInterpreter)
          ).rethrow 

  yield new (ReportAction ~> F):
        def apply[A](action: ReportAction[A]) = action
          .value
          .compile(applicationInterpreter)
          .foldMap(composedExecutor)
          .flatMap {
            case Right(value) => Monad[F].pure(value)
            case Left(err) => Async[F].raiseError(InfrastructureError.InternalError(err))
          }

  def server[F[_]: Async: Console](service: ReportAction ~> F) = {
    for
      config <- Stream.eval { ConfigSource.default.at("jwt").load[Configuration] match
        case Right(config) => 
          config.pure
        case Left(err) => 
          Exception("Bad authentication configuration").raiseError
      }
      code <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"3500")
          .withHttpApp(Router(
            "/reports" -> ReportService.service(config, service)
          ).orNotFound)
          .withIdleTimeout(Duration.Inf)
          .build >>
        Resource.eval(Async[F].never)
      )
    yield code
  }.drain

  def run(args: List[String]): InfrastructureResult[ExitCode] =
    // Run Program
    factory[InfrastructureResult].use { service =>
      server[InfrastructureResult](service).compile.drain.as(ExitCode.Success)
    }
}
