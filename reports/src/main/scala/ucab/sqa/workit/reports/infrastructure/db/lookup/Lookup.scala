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
    def getReport(id: UUID): ParallelInstruction[F, Either[Throwable, Option[ReportModel]]]
    def getReportsByTraining(id: String): ParallelInstruction[F, Either[Throwable, Vector[ReportModel]]]
    def getReportIssuedByUserOnTraining(id: String, trainingId: String): ParallelInstruction[F, Either[Throwable, Option[ReportModel]]]
    def getAllReports: ParallelInstruction[F, Either[Throwable, Vector[ReportModel]]]

class LookupLanguage[F[_]](using injector: InjectK[LookupAction, F]) extends LookupOps[F]:
    def getReport(id: UUID) = FreeApplicative.lift(LookupAction.GetReport(id)).compile(injector.inj)
    def getReportsByTraining(id: String) = FreeApplicative.lift(LookupAction.GetReportsByTraining(id)).compile(injector.inj)
    def getAllReports = FreeApplicative.lift(LookupAction.GetAllReports).compile(injector.inj)
    def getReportIssuedByUserOnTraining(id: String, trainingId: String) =
            FreeApplicative.lift(LookupAction.GetReportIssuedByUserOnTraining(id, trainingId)).compile(injector.inj)