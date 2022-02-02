package ucab.sqa.workit.reports.domain.values

import java.util.UUID
import cats.implicits._
import ucab.sqa.workit.reports.domain.errors.InvalidUUIDError

final case class ReportId private[domain] (id: UUID)

private[domain] object ReportId {
    def apply(id: String) = 
        Either
        .catchNonFatal(UUID.fromString(id))
        .map(new ReportId(_)) 
        .leftMap(InvalidUUIDError(_))
}