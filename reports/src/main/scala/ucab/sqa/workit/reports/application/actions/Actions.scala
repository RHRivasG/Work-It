package ucab.sqa.workit.reports.application.actions

import ucab.sqa.workit.reports.domain.Report
import ucab.sqa.workit.reports.domain.events.ReportEvent

sealed trait ReportApplicationAction[A]
final case class GetReport(id: String) extends ReportApplicationAction[Report]
final case class GetReportByTrainer(id: String) extends ReportApplicationAction[Report]
final case class Handle(evt: ReportEvent) extends ReportApplicationAction[Unit]
final case object GetAllReports extends ReportApplicationAction[Vector[Report]]