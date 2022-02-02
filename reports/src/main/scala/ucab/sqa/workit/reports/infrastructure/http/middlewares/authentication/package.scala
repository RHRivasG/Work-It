package ucab.sqa.workit.reports.infrastructure.http.middlewares

import cats.implicits._
import cats.data.Kleisli
import org.http4s.Request
import org.http4s.headers.Authorization
import cats.Monad
import cats.effect.std.Console
import ucab.sqa.workit.reports.infrastructure.errors.ReportError
import ucab.sqa.workit.reports.infrastructure.errors.ReportInfrastructureError
import cats.MonadError
import org.http4s.AuthScheme
import pdi.jwt.JwtCirce
import pdi.jwt.JwtAlgorithm
import ucab.sqa.workit.reports.infrastructure.FromEval
import cats.Eval
import org.http4s.server.AuthMiddleware
import cats.data.OptionT
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl
import cats.Applicative
import ucab.sqa.workit.reports.infrastructure.errors.ReportDomainError

package object authentication {
    def extractRoles[F[_]: FromEval: MonadError[*[*], ReportError]: Console](config: Configuration): Kleisli[F, Request[F], Vector[String]] = 
        Kleisli(request => { for {
            header <- request.headers.get[Authorization] match {
                case Some(value) => Monad[F].pure(value)
                case None => MonadError[F, ReportError].raiseError(ReportInfrastructureError(new Exception("Missing authorization header")))
            }
            creds = header.credentials
            token = creds.renderString
            _ <- if (creds.authScheme == AuthScheme.OAuth) 
                    MonadError[F, ReportError].raiseError(ReportInfrastructureError(new Exception("Wrong authorization scheme")))
                 else 
                    Monad[F].pure(())
            _ <- Console[F].println(f"Received attempt to authentication with credentials: ${token}")
            token <- MonadError[F, ReportError].rethrow(
                FromEval[F].fromEval(
                    Eval.later(
                        Either.fromTry(
                            JwtCirce.decodeJson(token.replace("Bearer ", ""), config.secretKey, Seq(JwtAlgorithm.HS512))
                        ).left.map[ReportError](ReportInfrastructureError(_))
                    )
                )
            )
            roles <- token.findAllByKey("roles").headOption.flatMap(_.asArray.flatMap(_.traverse(_.asString))) match {
                case Some(roles) if token.findAllByKey("sub").headOption.flatMap(_.asString) == Some("admin") => Monad[F].pure(roles ++ Seq("admin"))
                case Some(roles) => Monad[F].pure(roles)
                case _ => MonadError[F, ReportError].raiseError(ReportInfrastructureError(new Exception("Wrong credentials, user is not admin")))
            }
            _ <- Console[F].println(f"Roles extracted: ${roles}")
            } yield roles }
        )

    def errorRoute[F[_]: Applicative]: AuthedRoutes[Throwable, F] = {
        val dsl = new Http4sDsl[F]{}
        import dsl._
        Kleisli(req => OptionT.liftF(Forbidden(req.context.getMessage())))
    }
    
    def admin[F[_]: FromEval: MonadError[*[*], ReportError]: Console](config: Configuration) = 
            AuthMiddleware((for {
                roles <- extractRoles[F](config)
                _ <- Kleisli.liftF(
                    if (!roles.contains("admin")) MonadError[F, ReportError].raiseError(ReportInfrastructureError(new Exception("Credentials invalid, user is not admin")))
                    else MonadError[F, ReportError].pure(roles)
                )
            } yield ()).mapF[F, Either[Throwable, Unit]](_.attemptT.leftSemiflatTap(Console[F].println).leftMap { _ match {
                case ReportDomainError(inner) => new Error(inner.toString())
                case err @ ReportInfrastructureError(_) => err.inner
            }}.value), 
            errorRoute[F]
            )

    
    def participantOrTrainer[F[_]: FromEval: MonadError[*[*], ReportError]: Console](config: Configuration) = 
            AuthMiddleware((for {
                roles <- extractRoles[F](config)
                _ <- Kleisli.liftF(
                    if (!roles.contains("trainer") && !roles.contains("participant")) MonadError[F, ReportError].raiseError(ReportInfrastructureError(new Exception("Credentials invalid, user is not logged in")))
                    else MonadError[F, ReportError].pure(roles)
                )
            } yield ()).mapF[F, Either[Throwable, Unit]](_.attemptT.leftMap { _ match {
                case ReportDomainError(inner) => new Error(inner.toString())
                case err @ ReportInfrastructureError(_) => err.inner
            }}.value), 
            errorRoute[F]
            )
}