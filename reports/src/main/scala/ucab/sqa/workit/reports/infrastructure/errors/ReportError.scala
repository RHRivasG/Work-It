package ucab.sqa.workit.reports.infrastructure.errors

import ucab.sqa.workit.reports.domain.errors.DomainError

sealed trait ReportError extends Throwable
final case class ReportDomainError(inner: DomainError) extends ReportError
final case class ReportInfrastructureError(inner: Throwable) extends ReportError