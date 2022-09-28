package ucab.sqa.workit.reports.domain.errors

import cats.Applicative
import cats.data.ValidatedNel
import cats.ApplicativeError
import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*

opaque type ReportResult[A] = ValidatedNel[DomainError, A]

object ReportResult:
    given (using ap: Applicative[[A] =>> ValidatedNel[DomainError, A]]): Applicative[ReportResult] = ap
    given (using ap: ApplicativeError[[A] =>> ValidatedNel[DomainError, A], DomainError]): ApplicativeError[ReportResult, DomainError] = 
        ap
    given (using ap: Monad[[A] =>> ValidatedNel[DomainError, A]]): Monad[ReportResult] = ap

    def failure[A](err: DomainError): ReportResult[A] = err.invalidNel

    def apply[A](result: => ValidatedNel[DomainError, A]): ReportResult[A] = result

    extension [A](result: ReportResult[A])
        def asEither: Either[NonEmptyList[DomainError], A] = result.toEither