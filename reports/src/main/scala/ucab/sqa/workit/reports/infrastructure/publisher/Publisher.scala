package ucab.sqa.workit.reports.infrastructure.publisher

import ucab.sqa.workit.reports.infrastructure.interpreter.*
import java.util.UUID
import cats.InjectK
import cats.free.Free

enum PublisherEvent:
    case ReportAcceptedEvent(trainingId: UUID)

enum PublisherAction[A]:
    case Publish(evt: PublisherEvent) extends PublisherAction[Unit]

trait PublisherOps[F[_]]:
    def publish(evt: PublisherEvent): Instruction[F, Unit]

class PublisherLanguage[F[_]](using injector: InjectK[PublisherAction, F]) extends PublisherOps[F]:
    def publish(evt: PublisherEvent) = Free.liftInject(PublisherAction.Publish(evt))