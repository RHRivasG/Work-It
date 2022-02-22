package ucab.sqa.workit.reports.infrastructure

import cats.~>
import cats.implicits.*
import cats.syntax.all.*
import cats.free.Free
import cats.data.EitherK
import ucab.sqa.workit.reports.infrastructure.db.*
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.application.*
import ucab.sqa.workit.reports.application.commands.ReportCommand
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.infrastructure.log.LoggerLanguage
import ucab.sqa.workit.reports.infrastructure.log.LogAction
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupAction
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupLanguage
import cats.free.FreeApplicative
import cats.data.Kleisli
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.data.EitherT
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemanticLanguage
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemantic
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationAction
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationLanguage
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherAction
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherLanguage
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherEvent

package object interpreter:
    private type ProgramStep[F[_], A] = Free[F, A]
    private type InterpreterAction0[A] = EitherK[LogAction, DatabaseAction, A]
    private type InterpreterAction1[A] = EitherK[NotificationAction, InterpreterAction0, A]
    private type InterpreterAction[A] = EitherK[PublisherAction, InterpreterAction1, A]
    private type InterpreterActionSemantic[A] = EitherK[
        [A] =>> ExecutionSemantic[InterpreterAction, A], 
        InterpreterAction, 
        A
    ]

    type Instruction[F[_], A] = ProgramStep[F,  A]
    type InterpreterInstruction[A] = ProgramStep[InterpreterActionSemantic,  A]
    type InterpreterLanguage[A] = EitherT[InterpreterInstruction, Throwable, A]

    private val DatabaseOps = DatabaseLanguage[InterpreterAction]
    private val LogOps = LoggerLanguage[InterpreterAction]
    private val NotificationOps = NotificationLanguage[InterpreterAction]
    private val PublisherOps = PublisherLanguage[InterpreterAction]
    private val ExecutionOps = ExecutionSemanticLanguage[InterpreterAction, InterpreterActionSemantic]

    import DatabaseOps.*
    import LogOps.*
    import ExecutionOps.*
    import NotificationOps.*
    import PublisherOps.*

    private def right[A](a: InterpreterInstruction[A]): InterpreterLanguage[A] = EitherT.right(a)
    private def of[A](a: InterpreterInstruction[Either[Throwable, A]]): InterpreterLanguage[A] = EitherT(a)
    private def rethrow[A](a: Either[Throwable, A]): InterpreterLanguage[A] = EitherT.fromEither(a)

    private def notifyStream = for 
        result <- getAllReports 
        () <- result match
            case Right(models) => log(f"Notifiying of dataset change") >> emit(models)
            case Left (e) => log(f"Error occured while notifiying $e")
    yield ()

    private def logAction[A](action: Free[InterpreterAction, A], msg: String) =
        right(parallel(action, log(msg)))

    private def logResult[A](result: Either[Throwable, A], msg: A => String) = result match
        case Right(value) => right(log(msg(value)).inject)
        case Left(err) => right(log(f"Occured error $err").inject)

    private val queriesInterpreter = new (ReportQuery ~> InterpreterLanguage):
        def apply[A](query: ReportQuery[A]) = query match
            case ReportQuery.GetReportByTraining(trainerId) => for {
                (result, _) <- logAction(getReportsByTraining(trainerId), f"Searching reports with training id $trainerId")
                () <- logResult(result, rc => f"Obtained ${rc.length} reports")
                models <- rethrow(result)
            } yield models
            case ReportQuery.GetReport(id) => for {
                (result, _) <- logAction(getReport(id), f"Searching report with id $id")
                () <- logResult(result, r => f"Obtained report $r")
                model <- rethrow(result)
            } yield model
            case ReportQuery.GetAllReports => for {
                (result, _) <- logAction(getAllReports, "Searching all reports")
                _ <- logResult(result, rc => f"Obtained ${rc.length} reports")
                models <- rethrow(result)
            } yield models
            case ReportQuery.GetReportIssuedByUserOnTraining(trainingId, userId) => for {
                (result, _) <- logAction(
                    getReportIssuedByUserOnTraining(userId, trainingId),
                    f"Searching reports issued by user with id $userId on training: $trainingId"
                )
                () <- logResult(result, r => f"Obtained report $r")
                models <- rethrow(result)
            } yield models
    
    private val commandsInterpreter = new (ReportCommand ~> InterpreterLanguage):
        def apply[A](command: ReportCommand[A]) = command match
            case ReportCommand(ReportEvent.ReportIssuedEvent(id, trainingId, issuer, reason)) => for {
                (result, _) <- logAction(
                    storeReport(ReportStorageInformation(id, trainingId, issuer, reason)),
                    f"Storing report with id $id issued by user with id $issuer on training: $trainingId and reason $reason"
                )
                () <- logResult(result, _ => f"Store operation successfull")
                () <- rethrow(result) 
                () <- right(spawn(notifyStream))
            } yield ()
            case ReportCommand(ReportEvent.ReportAcceptedEvent(id, trainingId)) => for {
                (result, _) <- logAction(deleteReport(id), f"Accepting report with id $id")
                () <- logResult(result, _ => f"Delete operation successfull")
                () <- rethrow(result)
                () <- right(spawn(notifyStream))
                () <- right(spawn(publish(PublisherEvent.ReportAcceptedEvent(trainingId))))
            } yield ()
            case ReportCommand(ReportEvent.ReportRejectedEvent(id)) => for {
                (result, _) <- logAction(deleteReport(id), f"Rejecting report with id $id")
                () <- logResult(result, _ => f"Delete operation successfull")
            } yield ()

    val applicationInterpreter = commandsInterpreter or queriesInterpreter