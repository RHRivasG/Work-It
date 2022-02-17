package ucab.sqa.workit.reports.infrastructure.db

import cats.InjectK
import cats.data.EitherK
import ucab.sqa.workit.reports.infrastructure.db.store.StoreAction
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupAction
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupLanguage
import ucab.sqa.workit.reports.infrastructure.db.store.StoreLanguage
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.free.FreeApplicative
import java.util.UUID

final case class ReportStorageInformation(id: UUID, trainingId: UUID, issuerId: UUID, reason: String)

type DatabaseAction[A] = EitherK[StoreAction, LookupAction, A]

trait DatabaseOps[F[_]]:
    def getReport(id: UUID): ParallelInstruction[F, Either[Throwable, Option[ReportModel]]]
    def getReportsByTraining(id: String): ParallelInstruction[F, Either[Throwable, Vector[ReportModel]]]
    def getReportIssuedByUserOnTraining(id: String, trainingId: String): ParallelInstruction[F, Either[Throwable, Option[ReportModel]]]
    def getAllReports: ParallelInstruction[F, Either[Throwable, Vector[ReportModel]]]
    def storeReport(reportInformation: ReportStorageInformation): ParallelInstruction[F, Either[Throwable, Unit]]
    def deleteReport(id: UUID): ParallelInstruction[F, Either[Throwable, Unit]]

class DatabaseLanguage[F[_]](using injector: InjectK[DatabaseAction, F]) extends DatabaseOps[F]:
    private val Lookup = LookupLanguage[DatabaseAction]
    private val Store = StoreLanguage[DatabaseAction]

    def getReport(id: UUID) = Lookup.getReport(id).compile(injector.inj)
    def getReportsByTraining(id: String) = Lookup.getReportsByTraining(id).compile(injector.inj)
    def getReportIssuedByUserOnTraining(id: String, trainingId: String) = Lookup.getReportIssuedByUserOnTraining(id, trainingId).compile(injector.inj)
    def getAllReports = Lookup.getAllReports.compile(injector.inj)
    def storeReport(reportInformation: ReportStorageInformation) = Store.storeReport(reportInformation).compile(injector.inj)
    def deleteReport(id: UUID) = Store.deleteReport(id).compile(injector.inj)