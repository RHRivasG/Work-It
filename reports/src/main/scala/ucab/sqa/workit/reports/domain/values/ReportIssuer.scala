package ucab.sqa.workit.reports.domain.values

import java.util.UUID
import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.domain.errors.ReportResult

opaque type ReportIssuer = UUID
object ReportIssuer:
    def apply(id: UUID): ReportIssuer = id

    def apply(id: String): ReportResult[ReportIssuer] = ReportResult {
        Either
        .catchNonFatal(UUIDFactory.fromString(id))
        .leftMap(_ => DomainError.InvalidUUIDError(id))
        .toValidatedNel
    }
        
    extension (issuer: ReportIssuer)
        def value: UUID = issuer
