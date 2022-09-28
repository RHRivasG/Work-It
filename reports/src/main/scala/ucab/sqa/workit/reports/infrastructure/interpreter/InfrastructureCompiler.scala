package ucab.sqa.workit.reports.infrastructure

import cats.~>
import cats.implicits.*
import cats.syntax.all.*
import cats.free.Free
import cats.data.EitherK
import ucab.sqa.workit.reports.infrastructure.db.*
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.application.*
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
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import ucab.sqa.workit.reports.infrastructure.interpreter.InfrastructureLanguage

object InfrastructureCompiler extends (ReportInput ~> InfrastructureLanguage):
    private def queriesInterpreter = new (ReportQuery ~> InfrastructureLanguage):
        def apply[A](query: ReportQuery[A]) = query match
            case ReportQuery.GetReportByTraining(training) => 
                InfrastructureDSL.findReportsOfTraining(training)
            case ReportQuery.GetReport(id) => 
                InfrastructureDSL.findReport(id)
            case ReportQuery.GetAllReports => 
                InfrastructureDSL.reports
            case ReportQuery.GetReportIssuedByUserOnTraining(userId, trainingId) => 
                InfrastructureDSL.reportIssuedByUserOnTraining(userId, trainingId)
    
    private def commandsInterpreter = new (ReportEvent ~> InfrastructureLanguage):
        def apply[A](command: ReportEvent[A]) = command match
            case ReportEvent.ReportIssuedEvent(id, issuer, training, reason) => 
                InfrastructureDSL.issueReport(id, training, issuer, reason)
            case ReportEvent.ReportAcceptedEvent(id, trainingId) => 
                InfrastructureDSL.acceptReport(id, trainingId)
            case ReportEvent.ReportRejectedEvent(id) => 
                InfrastructureDSL.rejectReport(id)

    def apply[A](act: ReportInput[A]) = (commandsInterpreter or queriesInterpreter)(act)