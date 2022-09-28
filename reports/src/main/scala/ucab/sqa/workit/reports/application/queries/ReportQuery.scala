package ucab.sqa.workit.reports.application.queries

import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.free.Free
import cats.InjectK
import java.util.UUID

enum ReportQuery[A]:
    case GetReport private[application](id: UUID) extends ReportQuery[Option[ReportModel]]
    case GetReportByTraining private[application](id: String) extends ReportQuery[Vector[ReportModel]]
    case GetAllReports extends ReportQuery[Vector[ReportModel]] 
    case GetReportIssuedByUserOnTraining private[application](issuerId: String, trainingId: String) extends ReportQuery[Option[ReportModel]]

trait ReportQueryOps[F[_]]:
    def getReport(id: UUID): Free[F, Option[ReportModel]]
    def getReportByTraining(id: String): Free[F, Vector[ReportModel]]
    def getAllReports: Free[F, Vector[ReportModel]]
    def getReportIssuedByUserOnTraining(issuerId: String, trainingId: String): Free[F, Option[ReportModel]]

class ReportQueryOpsImpl[F[_]](using injector: InjectK[ReportQuery, F]) extends ReportQueryOps[F]:
    def getReport(id: UUID) = Free.liftInject(ReportQuery.GetReport(id))
    def getReportByTraining(id: String) = Free.liftInject(ReportQuery.GetReportByTraining(id))
    def getAllReports = Free.liftInject(ReportQuery.GetAllReports)
    def getReportIssuedByUserOnTraining(issuerId: String, trainingId: String) =
        Free.liftInject(ReportQuery.GetReportIssuedByUserOnTraining(issuerId, trainingId))