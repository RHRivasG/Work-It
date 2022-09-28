package ucab.sqa.workit.reports.domain

import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.domain.errors.ReportResult
import ucab.sqa.workit.reports.domain.values.ReportId
import ucab.sqa.workit.reports.domain.values.ReportIssuer
import ucab.sqa.workit.reports.domain.values.ReportReason
import ucab.sqa.workit.reports.domain.values.Training
import java.util.UUID

final case class Report private(id: ReportId, issuer: ReportIssuer, training: Training, reason: ReportReason)

object Report:
    def complete(id: UUID, training: UUID, issuer: UUID, reason: String) =
        ReportReason(reason) map { Report(ReportId(id),  ReportIssuer(issuer), Training(training), _) }

    def of(id: String, issuer: String, training: String, reason: String) = (
        ReportId(id), 
        ReportIssuer(issuer),
        Training(training), 
        ReportReason(reason)
    ).mapN { (vid, vissuer, vtrainingId, vreason) => (
        ReportEvent.ReportIssuedEvent(vid.value, vissuer.value, vtrainingId.value, vreason.value), 
        Report(vid, vissuer, vtrainingId, vreason)
    )}

    def identified(issuer: String, training: String, reason: String) = (
        ReportIssuer(issuer),
        Training(training),
        ReportReason(reason)
    ).mapN { (vissuerId, vtrainingId, vreason) => 
        val vid = ReportId(UUID.randomUUID)
        (
            ReportEvent.ReportIssuedEvent(vid.value, vissuerId.value, vtrainingId.value, vreason.value), 
            Report(vid, vissuerId, vtrainingId, vreason)
        )
    }

    def unsafe(id: UUID, training: UUID, issuer: UUID, reason: String) = {
        val vid = ReportId(id)
        val vissuer = ReportIssuer(issuer)
        val vtrainingID = Training(training)
        val vreason = ReportReason.unsafe(reason)

        Report(vid, vissuer, vtrainingID, vreason)
    }

    extension (report: Report)
        def accept: ReportEvent[Unit] = ReportEvent.ReportAcceptedEvent(report.id.value, report.training.value)
        def reject: ReportEvent[Unit] = ReportEvent.ReportRejectedEvent(report.id.value)