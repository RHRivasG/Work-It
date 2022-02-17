package ucab.sqa.workit.reports.infrastructure

import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.data.NonEmptyList

enum InfrastructureError extends Throwable:
    case InternalError(error: NonEmptyList[DomainError])
    case AuthenticationError(error: Throwable)
    case LookupError(error: Throwable)
    case StoreError(error: Throwable)