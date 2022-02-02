package ucab.sqa.workit.reports.application.requests

import ucab.sqa.workit.reports.application.models.ReportModel

sealed trait ReportRequest[A]
final case class GetReport(id: String) extends ReportRequest[ReportModel]
final case class GetReportByTrainer(id: String) extends ReportRequest[ReportModel]
final case object GetAllReports extends ReportRequest[Vector[ReportModel]]
final case class IssueReport(trainingId: String, reason: String) extends ReportRequest[Unit]
final case class AcceptReport(id: String) extends ReportRequest[Unit]
final case class RejectReport(id: String) extends ReportRequest[Unit]