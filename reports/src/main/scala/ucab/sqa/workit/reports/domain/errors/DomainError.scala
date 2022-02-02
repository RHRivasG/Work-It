package ucab.sqa.workit.reports.domain.errors

sealed trait DomainError
final case class InvalidUUIDError(inner: Throwable) extends DomainError
final case class ReportNotFoundError(id: String) extends DomainError