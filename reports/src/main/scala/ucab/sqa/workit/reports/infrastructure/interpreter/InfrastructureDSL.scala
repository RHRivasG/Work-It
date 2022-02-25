package ucab.sqa.workit.reports.infrastructure.interpreter

import java.util.UUID
import cats.implicits.*
import cats.syntax.all.*
import cats.free.Free
import cats.data.EitherK
import ucab.sqa.workit.reports.infrastructure.interpreter.syntax.*
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemanticLanguage
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationLanguage
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherLanguage
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherEvent
import ucab.sqa.workit.reports.infrastructure.log.LoggerLanguage
import ucab.sqa.workit.reports.infrastructure.db.DatabaseLanguage
import ucab.sqa.workit.reports.infrastructure.db.ReportStorageInformation
import shapeless.the
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemanticOps

object InfrastructureDSL:
    // Module definitions
    private val DatabaseOps = DatabaseLanguage[InfrastructureAction]
    private val LogOps = LoggerLanguage[InfrastructureAction]
    private val NotificationOps = NotificationLanguage[InfrastructureAction]
    private val PublisherOps = PublisherLanguage[InfrastructureAction]
    private val ExecutionOps = ExecutionSemanticLanguage[InfrastructureAction]

    // Import modules
    import DatabaseOps.*
    import LogOps.*
    import NotificationOps.*
    import PublisherOps.*
    import ExecutionOps.*

    private def notifyStream = for 
        result <- getAllReports 
        () <- result match
            case Right(models) => log(f"Notifiying of dataset change") >> emit(models)
            case Left (e) => log(f"Error occured while notifiying $e")
    yield ()

    private def logAction[A](action: InfrastructureInstruction[A], msg: String): InfrastructureLanguage[(A, Unit)] = 
        (action, log(msg)).concurrent.dsl

    private def logResult[A](result: Either[Throwable, A], msg: A => String) = result match
        case Right(value) => log(msg(value)).dsl
        case Left(err) => log(f"Occured error $err").dsl

    def findReportsOfTraining(training: String) = for
        (result, _) <- logAction(getReportsByTraining(training), f"Searching reports with training id $training")
        () <- logResult(result, rc => f"Obtained ${rc.length} reports")
        models <- result.fail
    yield models

    def findReport(id: UUID) = for
        (result, _) <- logAction(getReport(id), f"searching report with id $id")
        () <- logResult(result, r => f"obtained report $r")
        model <- result.fail
    yield model

    def reports = for
        (result, _) <- logAction(getAllReports, "Searching all reports")
        _ <- logResult(result, rc => f"Obtained ${rc.length} reports")
        models <- result.fail
    yield models

    def reportIssuedByUserOnTraining(issuer: String, training: String) = for
        (result, _) <- logAction(
            getReportIssuedByUserOnTraining(issuer, training),
            f"Searching reports issued by user with id $issuer on training: $training"
        )
        () <- logResult(result, r => f"Obtained report $r")
        models <- result.fail
    yield models

    def issueReport(id: UUID, training: UUID, issuer: UUID, reason: String) = for
        (result, _) <- logAction(
            storeReport(ReportStorageInformation(id, training, issuer, reason)),
            f"Storing report with id $id issued by user with id $issuer on training: $training and reason $reason"
        )
        () <- logResult(result, _ => f"Store operation successfull")
        () <- result.fail
        () <- notifyStream.background.dsl
    yield ()

    def acceptReport(id: UUID, training: UUID) = for
        (result, _) <- logAction(deleteReport(id), f"Accepting report with id $id")
        () <- logResult(result, _ => f"Delete operation successfull")
        () <- result.fail
        () <- notifyStream.background.dsl
        () <- publish(PublisherEvent.ReportAcceptedEvent(training)).background.dsl
    yield ()

    def rejectReport(id: UUID) = for
        (result, _) <- logAction(deleteReport(id), f"Rejecting report with id $id")
        () <- logResult(result, _ => f"Delete operation successfull")
    yield ()