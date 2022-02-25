package ucab.sqa.workit.reports

import cats.data.EitherK
import cats.free.Free
import cats.data.EitherT
import cats.data.NonEmptyList
import ucab.sqa.workit.reports.domain.events.ReportEvent
import ucab.sqa.workit.reports.application.queries.ReportQuery
import ucab.sqa.workit.reports.domain.errors.DomainError

package object application:
    type ReportInput[A] = EitherK[ReportEvent, ReportQuery, A]
    type ReportActionF[A] = Free[ReportInput, A]
    type ReportAction[A] = EitherT[ReportActionF, NonEmptyList[DomainError], A]
