package ucab.sqa.workit.reports.infrastructure.notifications

import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.free.Free
import cats.InjectK

enum NotificationAction[A]:
    case Notify(vector: Vector[ReportModel]) extends NotificationAction[Unit]

trait NotificationOps[F[_]]:
    def emit(vector: Vector[ReportModel]): Instruction[F, Unit]

class NotificationLanguage[F[_]](using injector: InjectK[NotificationAction, F]) extends NotificationOps[F]:
    def emit(vector: Vector[ReportModel]) = Free.liftInject(NotificationAction.Notify(vector))