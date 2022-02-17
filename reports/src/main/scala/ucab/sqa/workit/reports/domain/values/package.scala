package ucab.sqa.workit.reports.domain

import cats.*
import cats.implicits.*
import cats.derived.*
import cats.data.ValidatedNel
import ucab.sqa.workit.reports.domain.errors.DomainError
import java.util.UUID
import cats.Applicative
import cats.data.NonEmptyList

package object values:
    opaque type ReportResult[A] = ValidatedNel[DomainError, A]

    object ReportResult:
        given (using ap: Applicative[[A] =>> ValidatedNel[DomainError, A]]): Applicative[ReportResult] = ap
        given (using ap: ApplicativeError[[A] =>> ValidatedNel[DomainError, A], DomainError]): ApplicativeError[ReportResult, DomainError] = 
            ap
        given (using ap: Monad[[A] =>> ValidatedNel[DomainError, A]]): Monad[ReportResult] = ap

        def failure[A](err: DomainError): ReportResult[A] = err.invalidNel

    extension [A](result: ReportResult[A])
        def asEither: Either[NonEmptyList[DomainError], A] = result.toEither

    opaque type ReportId = UUID
    object ReportId:
        import ReportResult.*

        def apply(id: UUID): ReportId = id

        def apply(id: String): ReportResult[ReportId] = 
            Either
            .catchNonFatal(UUID.fromString(id))
            .leftMap(_ => DomainError.InvalidUUIDError(id))
            .toValidatedNel

        extension (reportId: ReportId)
            def value: UUID = reportId

    opaque type TrainingId = UUID
    object TrainingId:
        import ReportResult.*

        def apply(id: UUID): TrainingId = id

        def apply(id: String): ReportResult[TrainingId] = 
            Either
            .catchNonFatal(UUID.fromString(id))
            .leftMap(_ => DomainError.InvalidUUIDError(id))
            .toValidatedNel

        extension (id: TrainingId)
            def value: UUID = id

    opaque type ReportReason = String
    object ReportReason:
        def unsafe(reason: String): ReportReason = reason

        def apply(reason: String): ReportResult[ReportReason] =
            Either.cond(reason.length <= 255, reason, DomainError.ReasonMaxLengthSurpasedError(reason)).toValidatedNel *>
            Either.cond(reason.trim.length >= 0, reason, DomainError.ReasonEmptyError(reason)).toValidatedNel

        extension (reason: ReportReason)
            def value: String = reason
    
    opaque type ReportIssuer = UUID
    object ReportIssuer:
        def apply(id: UUID): ReportIssuer = id

        def apply(id: String): ReportResult[ReportIssuer] =
            Either
            .catchNonFatal(UUID.fromString(id))
            .leftMap(_ => DomainError.InvalidUUIDError(id))
            .toValidatedNel
        
        extension (issuer: ReportIssuer)
            def value: UUID = issuer