package ucab.sqa.workit.reports.domain

import java.util.UUID
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent
import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.values.*

final case class Report private(id: ReportId, issuerId: ReportIssuer, trainingId: TrainingId, reason: ReportReason)

object Report:
    def complete(id: UUID, trainingId: UUID, issuerId: UUID, reason: String) =
        ReportReason(reason) map { new Report(ReportId(id),  ReportIssuer(issuerId), TrainingId(trainingId), _) }

    def of(id: String, issuerId: String, trainingId: String, reason: String): ReportResult[(ReportEvent[Unit], Report)] = (
        ReportId(id), 
        ReportIssuer(issuerId),
        TrainingId(trainingId), 
        ReportReason(reason)
    ).mapN { (vid, vissuer, vtrainingId, vreason) => (
        ReportEvent.ReportIssuedEvent(vid.value, vissuer.value, vtrainingId.value, vreason.value), 
        Report(vid, vissuer, vtrainingId, vreason)
    )}

    def identified(issuerId: String, trainingId: String, reason: String): ReportResult[(ReportEvent[Unit], Report)] = (
        ReportIssuer(issuerId),
        TrainingId(trainingId),
        ReportReason(reason)
    ).mapN { (vissuerId, vtrainingId, vreason) => 
        val vid = ReportId(UUID.randomUUID)
        (
            ReportEvent.ReportIssuedEvent(vid.value, vissuerId.value, vtrainingId.value, vreason.value), 
            Report(vid, vissuerId, vtrainingId, vreason)
        )
    }

    def unsafe(id: UUID, trainingId: UUID, issuerId: UUID, reason: String) = {
        val vid = ReportId(id)
        val vissuer = ReportIssuer(issuerId)
        val vtrainingID = TrainingId(trainingId)
        val vreason = ReportReason.unsafe(reason)

        Report(vid, vissuer, vtrainingID, vreason)
    }

    extension (report: Report)
        def accept: ReportEvent[Unit] = ReportEvent.ReportAcceptedEvent(report.id.value, report.trainingId.value)
        def reject: ReportEvent[Unit] = ReportEvent.ReportRejectedEvent(report.id.value)