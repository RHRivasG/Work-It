package ucab.sqa.workit.reports.application.models

import ucab.sqa.workit.reports.domain.Report
import java.util.UUID

final case class ReportModel(id: UUID, issuerId: UUID, trainingid: UUID, reason: String)

object ReportModel:
    def apply(report: Report) =
        new ReportModel(report.id.value, report.issuerId.value, report.trainingId.value, report.reason.value)
    
    extension (reportModel: ReportModel)
        def toReport = Report.unsafe(reportModel.id, reportModel.issuerId, reportModel.trainingid, reportModel.reason)