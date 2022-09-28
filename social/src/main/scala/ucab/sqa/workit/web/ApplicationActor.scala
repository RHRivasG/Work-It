package ucab.sqa.workit.web

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import _root_.ucab.sqa.workit.application.ApplicationService
import cats._
import cats.implicits._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

sealed trait Request[+C, +Q[_], A]

final case class Notification[C, Q[_]](
    command: C
) extends Request[C, Q, Unit]

final case class Command[C, Q[_]](
    command: C,
    replyTo: ActorRef[Either[Error, Unit]]
) extends Request[C, Q, Unit]

final case class Query[C, Q[_], A](
    query: Q[A],
    replyTo: ActorRef[Either[Error, A]]
) extends Request[C, Q, A]

object ApplicationActor {
  def apply[F[_], C, Q[_]](applicationService: ApplicationService[F, C, Q])(
      implicit executor: F ~> Future
  ) = Behaviors.receive[Request[C, Q, _]] { (ctx, request) =>
    val logger = ctx.log

    request match {
      case Notification(cmd) =>
        applicationService.execute(cmd).run(executor).andThen {
          case Failure(e) =>
            logger.error("Error occured on notification!", e)
          case Success(Left(e)) =>
            logger.error("Error occured on notification!", e)
          case Success(Right(())) =>
            logger.info(
              f"Notification ${cmd.getClass.getName} processed succesfully"
            )
        }
        Behaviors.same
      case Command(cmd, replyTo) =>
        applicationService.execute(cmd).run(executor).andThen {
          case Success(either) => replyTo ! either
          case Failure(e)      => replyTo ! Left(new Error(e))
        }
        Behaviors.same
      case Query(qry, replyTo) =>
        applicationService.query(qry).run(executor).andThen {
          case Success(either) => replyTo ! either
          case Failure(e)      => replyTo ! Left(new Error(e))
        }
        Behaviors.same
    }
  }
}
