package ucab.sqa.workit.reports.infrastructure.db.lookup

import cats.free.Free
import cats.InjectK
import java.util.UUID
import cats.data.Kleisli
import cats.free.FreeApplicative
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.Parallel

enum LookupAction[A]:
    case GetReport(id: UUID) extends LookupAction[Either[Throwable, Option[ReportModel]]]
    case GetReportsByTraining(id: String) extends LookupAction[Either[Throwable, Vector[ReportModel]]]
    case GetReportIssuedByUserOnTraining(id: String, trainingId: String) extends LookupAction[Either[Throwable, Option[ReportModel]]]
    case GetAllReports extends LookupAction[Either[Throwable ,Vector[ReportModel]]]

trait LookupOps[F[_]]:
    def getReport(id: UUID): Instruction[F, Either[Throwable, Option[ReportModel]]]
    def getReportsByTraining(id: String): Instruction[F, Either[Throwable, Vector[ReportModel]]]
    def getReportIssuedByUserOnTraining(id: String, trainingId: String): Instruction[F, Either[Throwable, Option[ReportModel]]]
    def getAllReports: Instruction[F, Either[Throwable, Vector[ReportModel]]]

class LookupLanguage[F[_]](using injector: InjectK[LookupAction, F]) extends LookupOps[F]:
    def getReport(id: UUID) = Free.liftInject(LookupAction.GetReport(id))
    def getReportsByTraining(id: String) = Free.liftInject(LookupAction.GetReportsByTraining(id))
    def getAllReports = Free.liftInject(LookupAction.GetAllReports)
    def getReportIssuedByUserOnTraining(id: String, trainingId: String) = 
        Free.liftInject(LookupAction.GetReportIssuedByUserOnTraining(id, trainingId))