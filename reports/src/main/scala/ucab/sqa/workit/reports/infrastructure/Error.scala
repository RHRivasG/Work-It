package ucab.sqa.workit.reports.infrastructure

import cats.data.NonEmptyList
import ucab.sqa.workit.reports.domain.errors.DomainError

enum InfrastructureError extends Throwable:
    case InternalError(error: NonEmptyList[DomainError])
    case AuthenticationError(error: Throwable)
    case LookupError(error: Throwable)
    case StoreError(error: Throwable)
    case ParseError(error: Throwable)