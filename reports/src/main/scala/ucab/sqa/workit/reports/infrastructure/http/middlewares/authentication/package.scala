package ucab.sqa.workit.reports.infrastructure.http.middlewares

import cats.syntax.all.*
import cats.implicits.*
import cats.data.Kleisli
import org.http4s.Request
import org.http4s.headers.Authorization
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.implicits.*
import cats.Monad
import cats.effect.std.Console
import cats.MonadError
import org.http4s.AuthScheme
import pdi.jwt.JwtCirce
import pdi.jwt.JwtAlgorithm
import cats.Eval
import org.http4s.server.AuthMiddleware
import cats.data.OptionT
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl
import cats.Applicative
import ucab.sqa.workit.reports.infrastructure.InfrastructureError
import org.http4s.Challenge

package object authentication:
    def extractRoles[F[_]: [F[_]] =>> MonadError[F, Throwable]: Console](config: Configuration): Kleisli[F, Request[F], AuthModel] = 
        Kleisli((request: Request[F]) => 
            for
                header <- request.headers.get[Authorization] match {
                    case Some(value) => value.pure
                    case None => 
                        InfrastructureError.AuthenticationError(Exception("Missing authorization header"))
                        .raiseError
                }
                creds = header.credentials
                token = creds.renderString.replace("Bearer ", "")
                _ <- (creds.authScheme == AuthScheme.Bearer).pure.ifM(
                        ().pure,
                        InfrastructureError.AuthenticationError(Exception("Wrong authorization scheme")).raiseError
                    )
                extractedPayload <- Either
                    .fromTry(JwtCirce.decodeJson(token, config.secretKey, Seq(JwtAlgorithm.HS512)))
                    .leftMap(InfrastructureError.AuthenticationError(_))
                    .pure[F]
                    .rethrow
                payload <- extractedPayload.asObject match
                    case Some(obj) => obj.pure
                    case None => InfrastructureError.AuthenticationError(Exception(f"Payload ${extractedPayload.spaces2} is invalid")).raiseError
                extractedRoles = for 
                    field <- payload("roles")
                    arr <- field.asArray
                    fieldRoles <- arr.traverse(_.asString)
                yield fieldRoles
                id = for
                    field <- payload("sub")
                    sub <- field.asString
                yield sub
                model <- extractedRoles zip id match {
                    case Some((roles, id)) if id == "admin" && !roles.contains("admin") => AuthModel((roles ++ Vector("admin")), id).pure
                    case Some((roles, id)) => AuthModel(roles, id).pure
                    case _ => InfrastructureError.AuthenticationError(Exception("Wrong credentials, user is not authenticated")).raiseError
                }
            yield model
        )
    
    def checkRoles[F[_]](roles: String*)(config: Configuration)(using M: MonadError[F, Throwable], C: Console[F]) = for 
        extractedModel <- extractRoles[F](config).attempt
        model = extractedModel match
            case Right(model) if model.hasRoles(roles) => Right(model)
            case Right(_) => Left(InfrastructureError.AuthenticationError(Exception(f"User does not belong to the roles $roles")))
            case Left(e) => Left(e)
    yield model

    def errorRoute[F[_]: Monad: Console]: AuthedRoutes[Throwable, F] = {
        val dsl = new Http4sDsl[F]{}
        import dsl.*
        Kleisli { req => OptionT.liftF(
            req.context match
                case InfrastructureError.AuthenticationError(err) => Unauthorized(
                    `WWW-Authenticate`(Challenge("OAuth2", "reports", Map())),
                    err.getMessage
                )
                case _ => InternalServerError("Oops! Something went wrong, try again later!")
        )}
    }
        
    def adminAccess[F[_]](config: Configuration)(using M: MonadError[F, Throwable], C: Console[F]) = 
        val dsl = Http4sDsl[F]
        import dsl.*
        AuthMiddleware(checkRoles("admin")(config), errorRoute)

    def standardAccess[F[_]](config: Configuration)(using M: MonadError[F, Throwable], C: Console[F]) =
        val dsl = Http4sDsl[F]
        import dsl.*
        AuthMiddleware(checkRoles("admin", "participant", "trainer")(config), errorRoute)