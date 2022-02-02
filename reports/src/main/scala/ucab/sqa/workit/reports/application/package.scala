package ucab.sqa.workit.reports

import cats._
import cats.implicits._
import cats.free.FreeT
import ucab.sqa.workit.reports.application.actions.ReportApplicationAction
import ucab.sqa.workit.reports.domain.Report
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.application.actions.Handle
import ucab.sqa.workit.reports.application.requests.ReportRequest
import ucab.sqa.workit.reports.application.requests.AcceptReport
import ucab.sqa.workit.reports.application.requests.RejectReport
import ucab.sqa.workit.reports.application.requests.IssueReport
import ucab.sqa.workit.reports.application.requests.GetReport
import ucab.sqa.workit.reports.application.requests.GetAllReports
import ucab.sqa.workit.reports.application.requests.GetReportByTrainer
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.data.EitherT

package object application {
    type ReportApplicationService = ReportRequest ~> ReportApplicationCompleteAction
    type ReportApplicationResult[A] = EitherT[Eval, DomainError, A]
    type ReportApplicationCompleteAction[A] = FreeT[ReportApplicationAction, ReportApplicationResult, A]

    def get(id: String): ReportApplicationCompleteAction[Report] = 
        FreeT.liftInject(actions.GetReport(id))

    def getByTrainer(id: String): ReportApplicationCompleteAction[Report] = 
        FreeT.liftInject(actions.GetReportByTrainer(id))

    def getAll: ReportApplicationCompleteAction[Vector[Report]] = 
        FreeT.liftInject(actions.GetAllReports)

    def handle(evt: ReportEvent): ReportApplicationCompleteAction[Unit] =
        FreeT.liftInject(Handle(evt))

    def of[A](e: Either[DomainError, A]): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT.fromEither(e))

    def eval[A](e: Eval[A]): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT.liftF(e))

    def pure[A](e: A): ReportApplicationCompleteAction[A] =
        FreeT.pure(e)

    def later[A](e: A): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT.liftF(Eval.later { e }))

    def now[A](e: A): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT.liftF(Eval.always { e }))

    def laterT[A](e: Either[DomainError, A]): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT(Eval.later { e }))

    def nowT[A](e: Either[DomainError, A]): ReportApplicationCompleteAction[A] =
        FreeT.liftT(EitherT(Eval.always { e }))
    
    private def issueReport(trainingId: String, reason: String) = for {
        (evt, _) <- laterT(Report(trainingId, reason))
        () <- handle(evt)
    } yield ()

    private def acceptReport(id: String) = for {
        report <- get(id)
        evt = report.accept
        () <- handle(evt)
    } yield ()

    private def rejectReport(id: String) = for {
        report <- get(id)
        evt = report.reject
        () <- handle(evt)
    } yield ()

    implicit val applicationService = λ[ReportRequest ~> ReportApplicationCompleteAction](
        _ match {
            case AcceptReport(id) => acceptReport(id)
            case IssueReport(trainingId, reason) => issueReport(trainingId, reason)
            case RejectReport(id) => rejectReport(id)
            case GetReport(id) => get(id).map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason))
            case GetReportByTrainer(id) => getByTrainer(id).map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason))
            case GetAllReports => getAll.nested.map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason)).value
        }
    )
}
