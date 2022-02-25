package ucab.sqa.workit.reports.infrastructure.http.services

import cats.*
import cats.syntax.all.*
import cats.effect.*
import io.circe.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.*
import io.circe.generic.semiauto
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import org.http4s.EntityEncoder
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceEntityDecoder.*
import cats.effect.kernel.Sync
import cats.effect.kernel.Concurrent
import ucab.sqa.workit.reports.domain.errors.*
import cats.derived.auto.eq.given_Eq_A
import ucab.sqa.workit.reports.application.*
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.data.NonEmptyList
import org.http4s.Response
import org.http4s.EntityDecoder
import org.http4s.AuthedRoutes
import cats.effect.kernel.Async
import cats.effect.std.Console
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame.Text
import ucab.sqa.workit.reports.infrastructure.http.error.HttpErrorHandler

object ReportService {
    private case class ReportIssueForm(training: String, reason: String)
    private given Decoder[ReportIssueForm] = semiauto.deriveDecoder
    private given Encoder[ReportModel] = semiauto.deriveEncoder
    private given [F[_]: Concurrent]: EntityDecoder[F, ReportIssueForm] = jsonOf[F, ReportIssueForm]

    def service[F[_]](config: Configuration)(using C: Console[F], A: Async[F], UseCase: UseCase[?, F]) = {
        val dsl = Http4sDsl[F]
        val errorHandler = HttpErrorHandler[F]

        import dsl.*
        import errorHandler.*

        def unprivilegedRoutes =
            AuthedRoutes.of[AuthModel, F] {
                case GET -> Root / id as _ => for 
                    result <- UseCase.reportsOfTraining(id).attempt
                    response <- result match
                        case Right(models) => Ok(models.toList)
                        case Left(error) => ErrorHandler(error)
                yield response
                case ctx @ POST -> Root as user => for
                    parseResult <- ctx.req.as[ReportIssueForm].attempt
                    result <- parseResult.toOption match
                        case Some(form) => UseCase.issueReport(user.id, form.training, form.reason).attempt
                        case None => InfrastructureError.ParseError(Exception("Could not parse input")).asLeft.pure
                    response <- result match
                        case Right(()) => Ok("Report succesfully issued")
                        case Left(error) => ErrorHandler(error)
                yield response
            }

        def privilegedRoutes = 
            AuthedRoutes.of[AuthModel, F] {
                case POST -> Root / id / "accept" as _ => 
                    UseCase
                    .acceptReport(id)
                    .flatMap { Ok(_) }
                    .recoverWith(ErrorHandler)
                case POST -> Root / id / "reject" as _ => 
                    UseCase
                    .rejectReport(id)
                    .flatMap { Ok(_) }
                    .recoverWith(ErrorHandler)
            }
        
        Router("/" -> (standardAccess(config))(unprivilegedRoutes), "/admin" -> (adminAccess(config))(privilegedRoutes))
    }

    def stream[F[_]: Async](builder: WebSocketBuilder2[F])(using UseCase: UseCase[[A] =>> fs2.Stream[F, A], ?]) =
        val dsl = Http4sDsl[F]
        import dsl.*
        
        HttpRoutes.of[F] {
            case GET -> Root / "stream" => builder.build(
                UseCase.reportStream.map(s => Text(s.asJson.spaces2)), 
                _.void
            )
        }
}