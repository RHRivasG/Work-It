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

object ReportService {
    private case class ReportIssueForm(trainingId: String, reason: String)
    private case class Error(`type`: String, title: String, status: Int, instance: String, problems: List[Error])

    private given Decoder[ReportIssueForm] = semiauto.deriveDecoder
    private given Encoder[ReportModel] = semiauto.deriveEncoder
    private given Encoder[Error] = semiauto.deriveEncoder


    private given [F[_]: Concurrent]: EntityDecoder[F, ReportIssueForm] = jsonOf[F, ReportIssueForm]


    def service[F[_]](config: Configuration, execute: ReportAction ~> F)(using C: Console[F], A: Async[F]) = {
        val dsl = Http4sDsl[F]
        import dsl.*

        def errorHandling[F[_]](error: Throwable) = error match
            case InfrastructureError.InternalError(NonEmptyList(DomainError.ReportNotFoundError(id), _@_*)) => NotFound(Error(
                "Not found",
                f"Report with id $id not found",
                404,
                f"/reports/$id",
                List()
            ).asJson)
            case InfrastructureError.InternalError(NonEmptyList(DomainError.InvalidUUIDError(id), _@_*)) => BadRequest(Error(
                "Invalid ID",
                "ID is not an UUID",
                400,
                f"/reports",
                List()
            ).asJson)
            case InfrastructureError.InternalError(errors) => BadRequest(Error(
                "Multiple problems",
                "Multiple problems found with your request",
                400,
                f"/reports",
                errors.map { error => Error(error.getClass.getName, error.toString, 400, f"/reports", List()) }.toList
            ).asJson)
            case _ => InternalServerError("Oops! Something went wrong, try again later!")

        def unprivilegedRoutes =
            AuthedRoutes.of[AuthModel, F] {
                case GET -> Root / id as _ => for 
                    result <- execute(getReportsOfTraining(id)).attempt
                    response <- result match
                        case Right(models) => Ok(models.toList)
                        case Left(error) => errorHandling(error)
                yield response
                case ctx @ POST -> Root as user => for
                    form <- ctx.req.as[ReportIssueForm]
                    result <- execute(issueReport(user.id, form.trainingId, form.reason)).attempt
                    response <- result match
                        case Right(()) => Ok("Report succesfully issued")
                        case Left(error) => errorHandling(error)
                yield response
            }

        def privilegedRoutes = 
            AuthedRoutes.of[AuthModel, F] {
                case POST -> Root / id / "accept" as _ => 
                    execute(acceptReport(id))
                    .flatMap { Ok(_) }
                    .recoverWith(errorHandling)
                case POST -> Root / id / "reject" as _ => 
                    execute(rejectReport(id))
                    .flatMap { Ok(_) }
                    .recoverWith(errorHandling)
            }
        
        Router("/" -> (standardAccess(config))(unprivilegedRoutes), "/admin" -> (adminAccess(config))(privilegedRoutes))
    }
}