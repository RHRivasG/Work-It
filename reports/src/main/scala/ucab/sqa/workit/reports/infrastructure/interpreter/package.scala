package ucab.sqa.workit.reports.infrastructure

import cats.free.Free
import cats.data.EitherK
import cats.data.EitherT
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.application.*
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.infrastructure.db.DatabaseAction
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupAction
import ucab.sqa.workit.reports.infrastructure.log.LogAction
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationAction
import ucab.sqa.workit.reports.infrastructure.publisher.PublisherAction
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemantic


package object interpreter:
    // Type definitions
    private type InfrastructureAction0[A] = EitherK[LogAction, DatabaseAction, A]
    private type InfrastructureAction1[A] = EitherK[NotificationAction, InfrastructureAction0, A]
    private type InfrastructureAction2[A] = EitherK[PublisherAction, InfrastructureAction1, A]
    type InfrastructureAction[A] = EitherK[ExecutionSemantic, InfrastructureAction2, A]
    type Instruction[F[_], A] = Free[F,  A]
    type InfrastructureInstruction[A] = Free[InfrastructureAction,  A]
    type InfrastructureLanguage[A] = EitherT[InfrastructureInstruction, Throwable, A]