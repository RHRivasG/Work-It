package ucab.sqa.workit.web.auth

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import cats.implicits._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import pdi.jwt.JwtClaim
import pdi.jwt.JwtAlgorithm
import java.time.Instant
import pdi.jwt.JwtSprayJson
import ucab.sqa.workit.web.helpers
import scala.util.Success
import scala.util.Failure
import cats.data.EitherT

sealed trait AuthorityActions

case class IssueToken(subject: String, preferences: Seq[String], roles: Seq[String], replyTo: ActorRef[String]) extends AuthorityActions
case class ValidateToken[T](token: String, validationMethod: String => Future[Either[Error, T]], replyTo: ActorRef[Option[helpers.auth.AuthResult[T]]]) extends AuthorityActions

object AuthorityActor {
    private val algorithm = JwtAlgorithm.HS512

    private def encode(claims: JwtClaim, key: String) =
        JwtSprayJson.encode(claims, key, algorithm)
        
    private def decode(token: String, key: String) =
        JwtSprayJson.decode(token, key, Seq(algorithm)).toEither.left.map(new Error(_))

    def apply(key: String) = Behaviors.receive[AuthorityActions] { (ctx, action) => action match {
        case IssueToken(subject, preferences, roles, replyTo) => 
            val claims = JwtClaim(
              subject = Some(subject),
            ) ++ (("roles", roles)) ++ (("preferences", preferences))

            replyTo ! encode(claims, key)
            Behaviors.same
        
        case ValidateToken(token, validationMethod, replyTo) =>  
            val logger = ctx.log
            val subject = for {
                token <- EitherT.fromEither[Future](decode(token, key))
                subject <- EitherT.fromEither[Future](token.subject.toRight(new Error("Subject not present in token")))
                validationResult <- 
                    EitherT(validationMethod(subject)).map { helpers.auth.user(_) }
                    .orElse {
                        EitherT.cond[Future](subject == "admin", helpers.auth.admin[Any], new Error("Invalid token"))
                    }
            } yield validationResult

            subject.value.andThen {
                case Success(Left(e)) => 
                    logger.error(f"Token invalid with error", e) 
                    replyTo ! None
                case Success(Right(value)) => 
                    logger.info(f"Token valid with value: $value") 
                    replyTo ! Some(value)
                case Failure(e) => 
                    logger.error("Error occured while decoding token", e) 
                    replyTo ! None
            }

            Behaviors.same
        }
    }
}