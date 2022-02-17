package ucab.sqa.workit.reports

import cats.implicits.*
import cats.*
import cats.free.FreeT
import ucab.sqa.workit.reports.domain.values.*
import ucab.sqa.workit.reports.domain.values.ReportResult.*
import ucab.sqa.workit.reports.domain.Report
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.application.queries.ReportQueryOpsImpl
import ucab.sqa.workit.reports.application.commands.ReportCommand
import ucab.sqa.workit.reports.application.commands.ReportCommandOpsImpl
import cats.data.EitherT
import cats.data.EitherK
import cats.data.NonEmptyList
import cats.Applicative
import cats.free.Free
import cats.InjectK
import cats.Id
import cats.data.Nested
import ucab.sqa.workit.reports.application.models.ReportModel

package object application:
    type ReportInput[A] = EitherK[ReportCommand, ReportQuery, A]
    type ReportActionF[A] = Free[ReportInput, A]
    type ReportAction[A] = EitherT[ReportActionF, NonEmptyList[DomainError], A]

    private val Commands = new ReportCommandOpsImpl[ReportInput]
    private val Queries = new ReportQueryOpsImpl[ReportInput]

    private def pure[A](result: => A): ReportAction[A] =
        EitherT.pure(result)

    private def of[A](result: => ReportResult[A]): ReportAction[A] =
        EitherT.fromEither(result.asEither)

    private def raise[A](err: DomainError): ReportAction[A] = 
        EitherT.leftT(NonEmptyList.one(err))

    private def lift[A](free: Free[ReportInput, A]): ReportAction[A] =
        EitherT.right(free)

    private def getReportIssuedByUserOnTraining(issuerId: String, trainingId: String): ReportAction[Option[ReportModel]] =
        lift(Queries.getReportIssuedByUserOnTraining(issuerId, trainingId))

    private def findReport(id: String): ReportAction[Report] = 
        getReport(id).map(_.toReport)

    def getReport(id: String): ReportAction[ReportModel] = for
        vid <- of(ReportId(id))
        result <- lift(Queries.getReport(vid.value))
        model <- result match {
            case Some(model) => pure(model)
            case None => raise(DomainError.ReportNotFoundError(id))
        }
    yield model

    def getAllReports: ReportAction[Vector[ReportModel]] = 
        lift(Queries.getAllReports)

    def getReportsOfTraining(id: String): ReportAction[Vector[ReportModel]] = for
        vid <- of(TrainingId(id))
        result <- lift(Queries.getReportByTrainer(id))
    yield result

    def issueReport(issuerId: String, trainingId: String, reason: String): ReportAction[Unit] = for
        // alreadyIssuedReport <- getReportIssuedByUserOnTraining(issuerId, trainingId)
        // (reportIssued, report) <- alreadyIssuedReport.fold()(report => raise(DomainError.UserAlreadyReportedTrainingError(
        //     trainingId = report.trainingid.toString, 
        //     issuerId = report.issuerId.toString)
        // ))
        (reportIssued, report) <- of(Report.identified(
            issuerId = issuerId, 
            trainingId = trainingId,
            reason = reason
        ))
        _ <- lift(Commands.done(reportIssued))
    yield ()

    def acceptReport(id: String): ReportAction[Unit] = for
        report <- findReport(id)
        reportAccepted = report.accept
        _ <- lift(Commands.done(reportAccepted))
    yield ()

    def rejectReport(id: String): ReportAction[Unit] = for
        report <- findReport(id)
        reportRejected = report.reject
        _ <- lift(Commands.done(reportRejected))
    yield ()