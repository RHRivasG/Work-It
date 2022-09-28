package ucab.sqa.workit.reports.infrastructure.log.`scala-logging`

import cats.*
import cats.implicits.*
import cats.effect.implicits.*
import ucab.sqa.workit.reports.infrastructure.log.LogAction
import com.typesafe.scalalogging.Logger
import cats.effect.kernel.Async
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

class ScalaLogging[F[_]: Async](logger: Logger) extends (LogAction ~> F):
    def apply[A](action: LogAction[A]) = action match
        case LogAction.Log(msg) => Async[F].blocking(logger.info(msg))
    
object ScalaLogging:
    def apply[F[_]: Async] = new ScalaLogging[F](Logger("AppLogger"))