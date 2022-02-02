package ucab.sqa.workit.reports.domain

import ucab.sqa.workit.reports.domain.values.ReportId
import ucab.sqa.workit.reports.domain.values.TrainingId
import ucab.sqa.workit.reports.domain.events.ReportIssuedEvent
import ucab.sqa.workit.reports.domain.events.ReportRejectedEvent
import ucab.sqa.workit.reports.domain.events.ReportAcceptedEvent
import java.util.UUID
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent

final case class Report private(id: ReportId, trainingId: TrainingId, reason: String) {
    def accept = ReportAcceptedEvent(id.id, trainingId.id)
    def reject = ReportRejectedEvent(id.id)
}

object Report {
    def apply(_id: UUID, _trainingId: UUID, reason: String) = {
        val id = ReportId(_id)
        val trainingId = TrainingId(_trainingId)
        new Report(id, trainingId, reason)
    }
    def apply(id: String, trainingId: String, reason: String): Either[DomainError, (ReportEvent, Report)] = for {
        id <- ReportId(id)
        trainingId <- TrainingId(trainingId)
    } yield (ReportIssuedEvent(id.id, trainingId.id, reason), new Report(id, trainingId, reason))

    def apply(trainingId: String, reason: String): Either[DomainError, (ReportEvent, Report)] = for {
        trainingId <- TrainingId(trainingId)
        id = ReportId(UUID.randomUUID)
    } yield (ReportIssuedEvent(id.id, trainingId.id, reason), new Report(id, trainingId, reason)) 
}