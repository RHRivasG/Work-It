package ucab.sqa.workit.reports.infrastructure.notifications

import fs2.Stream
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.effect.kernel.Resource

trait NotificationHandler[F[_]] {
    def stream: Stream[F, Vector[ReportModel]]
    def send(models: Vector[ReportModel]): F[Unit]
    def processingStream: Stream[F, Unit]
}

object NotificationHandler {
    def resource[F[_]](implicit rs: Resource[F, NotificationHandler[F]]) = rs
}