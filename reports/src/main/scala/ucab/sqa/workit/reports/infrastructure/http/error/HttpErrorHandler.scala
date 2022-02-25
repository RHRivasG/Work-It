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
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.implicits.*
import cats.data.OptionT
import cats.data.EitherT
import org.http4s.Status
import org.http4s.EntityEncoder
import io.circe.Json
import org.http4s.Headers

class HttpErrorHandler[F[_]: Monad]:
    val dsl = Http4sDsl[F]
    import dsl.*

    private type HttpErrorHandler = Kleisli[F, Throwable, Response[F]]
    private given Encoder[ApiError] = semiauto.deriveEncoder

    object ErrorHandler extends PartialFunction[Throwable, F[Response[F]]]:
        def apply(e: Throwable) = {
            val error = summon[Fail[Throwable]].toError(true, e)
            Response(
                status = Status.fromInt(error.status.getOrElse(500)).getOrElse(InternalServerError.status), 
                body = EntityEncoder[F, Json].toEntity(error.asJson).body,
                headers = Headers(("Content-Type", "application/json"))
            ).pure
        }
        def isDefinedAt(e: Throwable) = true