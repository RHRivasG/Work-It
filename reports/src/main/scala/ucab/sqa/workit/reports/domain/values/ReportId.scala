package ucab.sqa.workit.reports.domain.values

import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.errors.ReportResult
import java.util.UUID

opaque type ReportId = UUID

object ReportId:

    def apply(id: UUID): ReportId = id

    def apply(id: String): ReportResult[ReportId] = ReportResult {
        Either
        .catchNonFatal(UUIDFactory.fromString(id))
        .leftMap(_ => DomainError.InvalidUUIDError(id))
        .toValidatedNel
    }

    extension (reportId: ReportId)
        def value: UUID = reportId
