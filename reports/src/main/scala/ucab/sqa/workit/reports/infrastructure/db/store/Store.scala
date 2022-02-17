package ucab.sqa.workit.reports.infrastructure.db.store

import java.util.UUID
import cats.free.Free
import cats.InjectK
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import ucab.sqa.workit.reports.infrastructure.db.ReportStorageInformation
import cats.data.Kleisli
import cats.free.FreeApplicative

enum StoreAction[A]:
    case StoreReport(id: UUID, trainingId: UUID, issuerId: UUID, reason: String) extends StoreAction[Either[Throwable, Unit]]
    case DeleteReport(id: UUID) extends StoreAction[Either[Throwable, Unit]]

trait StoreOps[F[_]]:
    def storeReport(info: ReportStorageInformation): ParallelInstruction[F, Either[Throwable, Unit]]
    def deleteReport(id: UUID): ParallelInstruction[F, Either[Throwable, Unit]]

class StoreLanguage[F[_]](using injector: InjectK[StoreAction, F]) extends StoreOps[F]:
    def storeReport(report: ReportStorageInformation) = report match
        case ReportStorageInformation(id, trainingId, issuerId, reason) =>
            FreeApplicative.lift(StoreAction.StoreReport(id, trainingId, issuerId, reason)).compile(injector.inj)

    def deleteReport(id: UUID) = FreeApplicative.lift(StoreAction.DeleteReport(id)).compile(injector.inj)
