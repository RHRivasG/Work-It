package ucab.sqa.workit.reports.domain.errors

import java.util.UUID
import cats.derived.semiauto
import cats.kernel.Eq

enum DomainError:
    case InvalidUUIDError(id: String)
    case ReportNotFoundError(id: String)
    case ReasonMaxLengthSurpasedError(reason: String)
    case ReasonEmptyError(reason: String)
    case UserAlreadyReportedTrainingError(trainingId: String, issuerId: String)

object DomainError:
    extension (error: DomainError)
        def toString = error match
            case DomainError.InvalidUUIDError(id) => f"The id given is not an uuid is invalid"
            case DomainError.ReportNotFoundError(id) => f"No report was found with id $id"
            case DomainError.ReasonEmptyError(reason) => f"The reason: $reason of the report is empty"
            case DomainError.UserAlreadyReportedTrainingError(trainingId, issuerId) => f"The user with id $issuerId already reported training $trainingId"