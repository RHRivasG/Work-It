package ucab.sqa.workit.reports.infrastructure.http.error

import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import cats.data.NonEmptyList
import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.syntax.all.*
import cats.data.Kleisli
import cats.Monad
import org.http4s.dsl.Http4sDsl
import org.http4s.Response
import io.circe.Encoder
import io.circe.generic.semiauto
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.implicits.*
import cats.data.OptionT
import cats.data.EitherT

class HttpErrorHandler[F[_]: Monad]:
    val dsl = Http4sDsl[F]
    import dsl.*

    private type HttpErrorHandler = Kleisli[[A] =>> EitherT[F, Throwable, A], Throwable, Response[F]]
    private type HttpErrorHandlerFull = Kleisli[F, Throwable, Response[F]]

    private case class ApiError(`type`: String, title: String, status: Int, instance: String, problems: List[ApiError])

    private given Encoder[ApiError] = semiauto.deriveEncoder

    private object Extractors:
        final case class ReportNotFound(id: String):
            def get = id
            def isEmpty = false

        object ReportNotFound:
            def unapply(nel: NonEmptyList[DomainError]): Option[ReportNotFound] = nel.find {
                case DomainError.ReportNotFoundError(id) => true
                case _ => false
            }.flatMap {
                case DomainError.ReportNotFoundError(id) => Some(ReportNotFound(id))
                case _ => None
            }

        final case class ParsingFailure(msg: String):
            def get = msg
            def isEmpty = false

        object ParsingFailure:
            def unapply(err: Throwable): Option[ParsingFailure] = err match
                case InfrastructureError.ParseError(err) => Some(ParsingFailure(err.getMessage))
                case _ => None

    import Extractors.*

    private val notFound: HttpErrorHandler = Kleisli {
        case InfrastructureError.InternalError(ReportNotFound(id)) => EitherT.liftF(NotFound(
            ApiError(
                "Not found",
                f"Report with id $id not found",
                404,
                f"/reports/$id",
                List()
            ).asJson
        ))
        case e => EitherT.leftT(e)
    }

    private val cannonicalErrors: HttpErrorHandler = Kleisli {
        case InfrastructureError.InternalError(NonEmptyList(err, List())) => EitherT.liftF(BadRequest(ApiError(
            err.getClass.getName,
            err.show,
            400,
            f"/reports",
            List()
        ).asJson))
        case InfrastructureError.InternalError(errors) => EitherT.liftF(BadRequest(ApiError(
            "Multiple problems",
            "Multiple problems found with your request",
            400,
            f"/reports",
            errors.map { error => ApiError(error.getClass.getName, error.show, 400, f"/reports", List()) }.toList
        ).asJson))
        case e => EitherT.leftT(e)
    }

    private val parseFailure: HttpErrorHandler = Kleisli {
        case ParsingFailure(msg) => EitherT.liftF(BadRequest(ApiError(
            "Parsing failure",
            "The request body you supplied could not be verified",
            400,
            f"/reports",
            List()
        ).asJson))
        case e => EitherT.leftT(e)
    }

    private val handler = (
        notFound <+> 
        cannonicalErrors <+>
        parseFailure
    )
    .orInternalServerError
    .partial

    object ErrorHandler extends PartialFunction[Throwable, F[Response[F]]]:
        def apply(e: Throwable) = handler(e)
        def isDefinedAt(e: Throwable) = true

    extension (handler: HttpErrorHandler)
        def orInternalServerError = Kleisli(handler
            .run
            .map(_.value)
            .map { _.flatMap {
                case Left(e) => InternalServerError(e.toString)
                case Right(r) => r.pure
            } }
        )

    extension (handler: HttpErrorHandlerFull)
        def partial = Function.unlift(handler.run.map(_.pure[Option]))