package ucab.sqa.workit.reports.infrastructure.http.services

import ucab.sqa.workit.reports.infrastructure.InfrastructureService
import cats.effect._
import cats.effect.std.Console
import cats.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.Encoder
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.domain.errors.ReportNotFoundError
import ucab.sqa.workit.reports.infrastructure.errors.ReportError
import ucab.sqa.workit.reports.infrastructure.errors.ReportDomainError
import ucab.sqa.workit.reports.domain.errors.InvalidUUIDError
import ucab.sqa.workit.reports.infrastructure.errors.ReportInfrastructureError
import ucab.sqa.workit.reports.application.requests.IssueReport
import ucab.sqa.workit.reports.application.requests.GetReportByTrainer
import ucab.sqa.workit.reports.application.requests.AcceptReport
import ucab.sqa.workit.reports.application.requests.RejectReport
// import ucab.sqa.workit.reports.application.requests.GetAllReports
import io.circe.Decoder
import cats.data.EitherT
import org.http4s.websocket.WebSocketFrame
import org.http4s.server.websocket.WebSocketBuilder2
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationHandler
import org.http4s.server.AuthMiddleware
import org.http4s.server.Router
import ucab.sqa.workit.reports.application.requests.GetAllReports

object ReportService {
    private case class ReportForm(trainingId: String, reason: String)

    private implicit val encoder = Encoder
        .forProduct3[ReportModel, String, String, String]("id", "trainingId", "reason")(report => (report.id, report.id, report.reason))

    private implicit val decoder = Decoder
        .forProduct2[ReportForm, String, String]("trainingId", "reason"){ case (trainingId, reason) =>  ReportForm(trainingId, reason) }

    private implicit def entityDecoder[F[_]: Concurrent] = jsonOf[F, ReportForm]

    def service[F[_]](service: InfrastructureService[F], adminAuth: AuthMiddleware[F, Unit], basicAuth: AuthMiddleware[F, Unit])(implicit Concurrent: Concurrent[F], Console: Console[F]) =  {
        val dsl = new Http4sDsl[F]{}
        import dsl._
        def errorHandler(pf: PartialFunction[AuthedRequest[F, Unit], F[Either[ReportError, F[Response[F]]]]]): PartialFunction[AuthedRequest[F, Unit], F[Response[F]]] = 
        {
            case request: AuthedRequest[F, Unit] => pf(request).flatMap(response => response match {
                case Right(r) => r
                case Left(ReportDomainError(ReportNotFoundError(id))) => NotFound(f"Report not found targeting training with id $id")
                case Left(ReportDomainError(InvalidUUIDError(_))) => BadRequest(f"The supplied uuid is invalid")
                case Left(ReportInfrastructureError(e)) => {
                    Console.errorln(e) >> 
                    InternalServerError("Oops! Something went wrong try again later")
                }
            })
        }
        Router[F](
            "/" -> basicAuth(AuthedRoutes.of { errorHandler {
                case GET -> Root / id as _ => service(GetReportByTrainer(id)).value.nested.map { s =>
                    Ok(s.asJson)
                }.value
                case authReq @ POST -> Root as _ => (for {
                    form <- EitherT.right(authReq.req.as[ReportForm])
                    () <- service(IssueReport(form.trainingId, form.reason))
                } yield Created("Report Issued")).value
            }}),
            "/admin" -> adminAuth(AuthedRoutes.of { errorHandler {
                case POST -> Root / id / "accept" as _ => (for {
                    () <- service(AcceptReport(id))
                } yield Ok("Report accepted")).value
                case POST -> Root / id / "reject" as _ => (for {
                    () <- service(RejectReport(id))
                } yield Ok("Report deleted")).value
            }})
        )
    }

    def websocket[F[_]: Async: Console](service: InfrastructureService[F], notificationHandler: NotificationHandler[F], websocket: WebSocketBuilder2[F]) = {
        val dsl = new Http4sDsl[F]{}
        def notify = for {
            models <- service(GetAllReports).value.rethrow.onError { err =>
                Console[F].errorln(f"An error occured ${err.getMessage}")
            }
            () <- notificationHandler.send(models)
        } yield ()
        import dsl._
        import fs2._
        HttpRoutes.of[F] {
            case GET -> Root / "reports" => 
                websocket
                .build(
                    notificationHandler.stream.map(m => WebSocketFrame.Text(m.asJson.spaces2)), 
                    s => Stream.eval(notify) >> s.as(())
                )
        }
    }
}