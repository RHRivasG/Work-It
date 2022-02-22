package ucab.sqa.workit.reports.domain.values

import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.errors.ReportResult
import java.util.UUID

opaque type Training = UUID
object Training:
    def apply(id: UUID): Training= id

    def apply(id: String): ReportResult[Training] = ReportResult {
        Either
        .catchNonFatal(UUID.fromString(id))
        .leftMap(_ => DomainError.InvalidUUIDError(id))
        .toValidatedNel
    }

    extension (id: Training)
        def value: UUID = id