package ucab.sqa.workit.reports.domain.events

import java.util.UUID

sealed trait ReportEvent 
final case class ReportIssuedEvent(id: UUID, trainingId: UUID, reason: String) extends ReportEvent
final case class ReportAcceptedEvent(id: UUID, trainingId: UUID) extends ReportEvent
final case class ReportRejectedEvent(id: UUID) extends ReportEvent