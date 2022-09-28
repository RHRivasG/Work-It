package ucab.sqa.workit.reports.infrastructure.log

import cats.*
import cats.implicits.*
import cats.data.EitherK
import ch.qos.logback.classic.joran.action.LoggerAction
import ucab.sqa.workit.reports.application.*
import cats.free.Free
import cats.InjectK
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.data.Kleisli
import cats.free.FreeApplicative

enum LogAction[A]:
    case Log(msg: String) extends LogAction[Unit]

trait LoggerOps[F[_]]:
    def log(msg: String): Instruction[F, Unit]

private class LoggerLanguage[F[_]](using injector: InjectK[LogAction, F]) extends LoggerOps[F]:
    def log(msg: String) = Free.liftInject(LogAction.Log(msg))