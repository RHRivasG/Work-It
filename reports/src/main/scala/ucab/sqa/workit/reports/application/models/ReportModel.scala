package ucab.sqa.workit.reports.application.models

import ucab.sqa.workit.reports.domain.Report
import java.util.UUID

final case class ReportModel(id: UUID, issuer: UUID, training: UUID, reason: String)

object ReportModel:
    def apply(report: Report) =
        new ReportModel(report.id.value, report.issuer.value, report.training.value, report.reason.value)
    
    extension (reportModel: ReportModel)
        def toReport = Report.unsafe(reportModel.id, reportModel.issuer, reportModel.training, reportModel.reason)