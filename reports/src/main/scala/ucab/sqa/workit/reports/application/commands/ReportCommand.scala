package ucab.sqa.workit.reports.application.commands

import ucab.sqa.workit.reports.domain.events.ReportEvent
import cats.free.Free
import cats.InjectK

trait ReportCommandOps[F[_]]:
    def done[A](evt: ReportEvent[A]): Free[F, A]

final case class ReportCommand[A] private[application] (evt: ReportEvent[A])

class ReportCommandOpsImpl[F[_]](using injector: InjectK[ReportCommand, F]) extends ReportCommandOps[F]:
    def done[A](evt: ReportEvent[A]) = Free.liftInject(ReportCommand(evt))
