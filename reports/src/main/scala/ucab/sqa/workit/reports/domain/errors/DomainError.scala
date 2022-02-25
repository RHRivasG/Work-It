package ucab.sqa.workit.reports.domain.errors

import java.util.UUID
import cats.derived.semiauto
import cats.kernel.Eq
import cats.Show

enum DomainError:
    case InvalidUUIDError(id: String)
    case ReportNotFoundError(id: String)
    case ReasonMaxLengthSurpasedError(reason: String)
    case ReasonEmptyError(reason: String)
    case UserAlreadyReportedTrainingError(trainingId: String, issuerId: String)

object DomainError:
    given Eq[DomainError] = semiauto.eq
    given Show[DomainError] = new Show:
        def show(error: DomainError) = error match
            case DomainError.InvalidUUIDError(id) => f"The id given is not an uuid is invalid"
            case DomainError.ReportNotFoundError(id) => f"No report was found with id $id"
            case DomainError.ReasonEmptyError(reason) => f"The reason: $reason of the report is empty"
            case DomainError.UserAlreadyReportedTrainingError(trainingId, issuerId) => f"The user with id $issuerId already reported training $trainingId"
            case DomainError.ReasonMaxLengthSurpasedError(reason) => f"The reason provided was greater than 255 characters: $reason"