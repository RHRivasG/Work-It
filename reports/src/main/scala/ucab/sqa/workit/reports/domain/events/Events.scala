package ucab.sqa.workit.reports.domain.events

import java.util.UUID

enum ReportEvent[A]:
    case ReportIssuedEvent(id: UUID, issuerId: UUID, trainingId: UUID, reason: String) extends ReportEvent[Unit]
    case ReportAcceptedEvent(id: UUID, trainingId: UUID) extends ReportEvent[Unit]
    case ReportRejectedEvent(id: UUID) extends ReportEvent[Unit]