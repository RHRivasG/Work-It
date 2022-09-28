package ucab.sqa.workit.reports.infrastructure.http.error

import cats.syntax.all.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import cats.data.NonEmptyList

trait Fail[A]:
    def toError(isSingle: Boolean, err: A): ApiError

object Fail:
    def apply[A](using fail: Fail[A]) = fail

given Fail[DomainError] with
    def toError(isSingle: Boolean, derror: DomainError) = derror match
        case DomainError.InvalidUUIDError(uuid) if isSingle => ApiError.singleton(
            "InvalidUUIDError",
            s"UUID $uuid is invalid",
            400,
            s"/reports/$uuid"
        )
        case DomainError.InvalidUUIDError(uuid) => ApiError.sub(
            "InvalidUUIDError",
            s"UUID $uuid is invalid",
        )
        case DomainError.ReasonEmptyError(_) => ApiError.sub(
            "ReasonEmptyError",
            "The provided reason is invalid",
        )
        case DomainError.ReasonMaxLengthSurpasedError(reason) => ApiError.sub(
            "ReasonMaxLengthSurpasedError",
            s"The reason $reason is over 255 characters",
        )
        case DomainError.UserAlreadyReportedTrainingError(trainingId, issuerId) => ApiError.singleton(
            "UserAlreadyReportedTrainingError",
            s"The user $issuerId already reported training $trainingId",
            403,
            "/reports"
        )
        case DomainError.ReportNotFoundError(id) => ApiError.singleton(
            "ReportNotFoundError",
            s"The report $id does not exist",
            404,
            f"/reports/$id"
        )

given Fail[InfrastructureError] with
    def toError(isSingle: Boolean, iferror: InfrastructureError) = iferror match
        case InfrastructureError.InternalError(NonEmptyList(err, List())) => summon[Fail[DomainError]].toError(true, err)
        case InfrastructureError.InternalError(errors) => {
            val instance = summon[Fail[DomainError]]
            ApiError(
                "MultipleProblemsError",
                "Multiple problems found with your request",
                400.some,
                "/reports".some,
                errors.map { instance.toError(false, _) }.toList
            )
        }
        case InfrastructureError.ParseError(err) => ApiError.singleton(
            "ParseError",
            err.getMessage,
            400,
            "/reports"
        )
        case _ => ApiError.singleton(
            "InternalServerError",
            "Oops! Something went wrong",
            500,
            "/"
        )

given Fail[Throwable] with
    def toError(isSingle: Boolean, iferror: Throwable) = iferror match
        case err: InfrastructureError => summon[Fail[InfrastructureError]].toError(true, err)
        case _ => ApiError.singleton(
            "InternalServerError",
            "Oops! Something went wrong",
            500,
            "/"
        )