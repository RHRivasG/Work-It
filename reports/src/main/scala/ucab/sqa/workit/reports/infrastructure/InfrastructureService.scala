package ucab.sqa.workit.reports.infrastructure

import cats._
import fs2.Stream
import cats.effect.kernel.Resource
import ucab.sqa.workit.reports.application.requests.ReportRequest
import ucab.sqa.workit.reports.domain.events.ReportEvent

trait InfrastructureService[F[_]] extends (ReportRequest ~> ReportIO[F, *]) {
    def eventStream: Stream[F, ReportEvent]
}

object InfrastructureService {
    def apply[F[_]](implicit service: InfrastructureService[F]) = service
    def resource[F[_]](implicit interpreter: Resource[F, InfrastructureService[F]]) = interpreter
}