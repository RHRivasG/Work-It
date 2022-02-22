package ucab.sqa.workit.reports.domain.values

import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.errors.ReportResult
import cats.syntax.all.*

opaque type ReportReason = String

object ReportReason:
    def unsafe(reason: String): ReportReason = reason

    def apply(reason: String): ReportResult[ReportReason] = ReportResult {
        Either.cond(reason.length <= 255, reason, DomainError.ReasonMaxLengthSurpasedError(reason)).toValidatedNel *>
        Either.cond(reason.trim.length > 0, reason, DomainError.ReasonEmptyError(reason)).toValidatedNel
    }

    extension (reason: ReportReason)
        def value: String = reason
