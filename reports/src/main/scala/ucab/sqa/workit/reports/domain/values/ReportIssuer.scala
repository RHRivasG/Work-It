package ucab.sqa.workit.reports.domain.values

import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.errors.ReportResult
import java.util.UUID

opaque type ReportIssuer = UUID
object ReportIssuer:
    def apply(id: UUID): ReportIssuer = id

    def apply(id: String): ReportResult[ReportIssuer] = ReportResult {
        Either
        .catchNonFatal(UUID.fromString(id))
        .leftMap(_ => DomainError.InvalidUUIDError(id))
        .toValidatedNel
    }
        
    extension (issuer: ReportIssuer)
        def value: UUID = issuer
