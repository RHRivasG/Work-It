package ucab.sqa.workit.reports.infrastructure

import cats.~>
import cats.implicits.*
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

package object interpreter:
    private type ProgramStep[F[_], A] = Free[[A] =>> FreeApplicative[F, A], A]
    private type InterpreterInstruction0[A] = DatabaseAction[A]
    type InterpreterInstructionAop[A] = EitherK[LogAction, InterpreterInstruction0, A]
    type ParallelInstruction[F[_], A] = FreeApplicative[F, A]
    type InterpreterInstruction[A] = ProgramStep[InterpreterInstructionAop,  A]
    type InterpreterAction[A] = InterpreterInstruction[Either[Throwable, A]]

    private val DatabaseOps = DatabaseLanguage[InterpreterInstructionAop]
    private val LogOps = LoggerLanguage[InterpreterInstructionAop]
    
    given [A]: Conversion[ParallelInstruction[InterpreterInstructionAop, A], InterpreterInstruction[A]] = Free.liftF(_)
    // given [A]: Conversion[SequentialInstruction[InterpreterInstructionAop, A], InterpreterInstruction[A]] = _.compile(
    //     new (InterpreterInstructionAop ~> ([A] =>> FreeApplicative[InterpreterInstructionAop, A])):
    //         def apply[A](inst: InterpreterInstructionAop[A]) = FreeApplicative.lift(inst)
    // )

    private val queriesInterpreter = new (ReportQuery ~> InterpreterAction):
        def apply[A](query: ReportQuery[A]) = query match
            case ReportQuery.GetReportByTrainer(trainerId) => for {
                result <- LogOps.log(f"Searching reports with training id $trainerId") *> DatabaseOps.getReportsByTraining(trainerId)
                _ <- result match
                    case Right(v) => LogOps.log(f"Received ${v.length} records")
                    case Left(e) => LogOps.log(f"Occured error $e")
            } yield result
            case ReportQuery.GetReport(id) => for {
                result <- LogOps.log(f"Searching report with id $id") *> DatabaseOps.getReport(id)
                () <- result match
                    case Right(value) => LogOps.log(f"Obtained report $result")
                    case Left(err) => LogOps.log(f"Occured error $err")
            } yield result
            case ReportQuery.GetAllReports => for {
                result <- LogOps.log("Searching all reports") *> DatabaseOps.getAllReports
                () <- result match 
                    case Right(value) => LogOps.log(f"Obtained reports $value")
                    case Left(err) => LogOps.log(f"Occured error $err")
            } yield result
            case ReportQuery.GetReportIssuedByUserOnTraining(trainingId, userId) => for {
                result <- LogOps.log(f"Searching reports issued by user with id $userId on training: $trainingId") *> 
                    DatabaseOps.getReportIssuedByUserOnTraining(userId, trainingId)
                () <- result match
                    case Right(value) => LogOps.log(f"Obtained reports $value")
                    case Left(err) => LogOps.log(f"Occured error $err")
            } yield result
    
    private val commandsInterpreter = new (ReportCommand ~> InterpreterAction):
        def apply[A](command: ReportCommand[A]) = command match
            case ReportCommand(ReportEvent.ReportIssuedEvent(id, trainingId, issuer, reason)) => for {
                result <- LogOps.log(f"Storing report with id $id issued by user with id $issuer on training: $trainingId and reason $reason") *> 
                    DatabaseOps.storeReport(ReportStorageInformation(id, trainingId, issuer, reason))
                _ <- result match
                    case Right(value) => LogOps.log(f"Store operation succesfull")
                    case Left(err) => LogOps.log(f"Occured error while storing $err")
            } yield result

    val applicationInterpreter = commandsInterpreter or queriesInterpreter