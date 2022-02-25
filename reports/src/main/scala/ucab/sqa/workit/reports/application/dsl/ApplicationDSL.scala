package ucab.sqa.workit.reports.application.dsl

import cats.implicits.*
import cats.*
import cats.free.FreeT
import ucab.sqa.workit.reports.domain.values.*
import ucab.sqa.workit.reports.domain.Report
import ucab.sqa.workit.reports.domain.errors.ReportResult
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.application.*
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.application.queries.ReportQueryOpsImpl
import ucab.sqa.workit.reports.application.commands.ReportCommandOpsImpl
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.data.EitherT
import cats.data.EitherK
import cats.data.NonEmptyList
import cats.Applicative
import cats.free.Free
import cats.InjectK
import cats.Id
import cats.data.Nested

object ApplicationDSL:
    private val Commands = new ReportCommandOpsImpl[ReportInput]
    private val Queries = new ReportQueryOpsImpl[ReportInput]

    import Commands.*
    import Queries.*

    private def pure[A](result: => A): ReportAction[A] =
        EitherT.pure(result)

    private def of[A](result: => ReportResult[A]): ReportAction[A] =
        EitherT.fromEither(result.asEither)

    private def raise[A](err: DomainError): ReportAction[A] = 
        EitherT.leftT(NonEmptyList.one(err))

    private def lift[A](free: Free[ReportInput, A]): ReportAction[A] =
        EitherT.right(free)

    private def reportIssued(issuerId: String, trainingId: String): ReportAction[Option[ReportModel]] =
        lift(getReportIssuedByUserOnTraining(issuerId, trainingId))

    private def findReport(id: String): ReportAction[Report] = 
        report(id).map(_.toReport)

    def report(id: String): ReportAction[ReportModel] = for
        vid <- of(ReportId(id))
        result <- lift(getReport(vid.value))
        model <- result match {
            case Some(model) => pure(model)
            case None => raise(DomainError.ReportNotFoundError(id))
        }
    yield model

    def reports: ReportAction[Vector[ReportModel]] = 
        lift(getAllReports)

    def reportsOfTraining(id: String): ReportAction[Vector[ReportModel]] = for
        vid <- of(Training(id))
        result <- lift(getReportByTraining(id))
    yield result

    def issueReport(issuer: String, training: String, reason: String): ReportAction[Unit] = for
        alreadyIssuedReport <- reportIssued(issuer, training)
        (reportIssued, report) <- alreadyIssuedReport match 
            case None => of(Report.identified(
                issuer = issuer, 
                training = training,
                reason = reason
            ))
            case Some(report) => raise(DomainError.UserAlreadyReportedTrainingError(
                trainingId = report.training.toString, 
                issuerId = report.issuer.toString)
            )
        _ <- lift(done(reportIssued))
    yield ()

    def acceptReport(id: String): ReportAction[Unit] = for
        report <- findReport(id)
        () <- lift(done(report.accept))
    yield ()

    def rejectReport(id: String): ReportAction[Unit] = for
        report <- findReport(id)
        () <- lift(done(report.reject))
    yield ()