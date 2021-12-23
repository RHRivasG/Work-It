package ucab.sqa.workit.web

import java.util.UUID
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import scala.util.Failure
import scala.util.Success
import cats.instances.future.catsStdInstancesForFuture
import _root_.ucab.sqa.workit.web.helpers.routes._
import _root_.ucab.sqa.workit.domain.participants.ParticipantEvent
import _root_.ucab.sqa.workit.web.participants.ParticipantRoutes
import _root_.ucab.sqa.workit.domain.participants.ParticipantDeletedEvent
import _root_.ucab.sqa.workit.domain.participants.valueobjects.ParticipantId
import _root_.ucab.sqa.workit.application.participants.ParticipantApplicationService
import _root_.ucab.sqa.workit.application.trainers.TrainerApplicationService
import _root_.ucab.sqa.workit.web.infrastructure
import _root_.ucab.sqa.workit.web.trainers.TrainerRoutes
import _root_.ucab.sqa.workit.web.auth.AuthRoutes

object QuickstartApp {
  private def startHttpServer(routes: Route)(implicit
      system: ActorSystem[_]
  ): Unit = {
    import system.executionContext

    val futureBinding = Http()
      .newServerAt("localhost", 5000)
      .bind(
        routes
      )
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(
          "Server online at http://{}:{}/",
          address.getHostString,
          address.getPort
        )
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  def main(args: Array[String]): Unit = {
    def infrastructureHandler(implicit system: ActorSystem[_]) = {
      val logger = system.log
      RejectionHandler
        .newBuilder()
        .handle { case InfrastructureRejection(e) =>
          logger.error("Error occured processing request!", e)
          complete(
            StatusCodes.InternalServerError,
            "Oops! Something happened! Please try again!"
          )
        }
        .result
    }
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      implicit val system = context.system
      implicit val timeout = Timeout.create(
        system.settings.config.getDuration("work-it-app.ipc.ask-timeout")
      )

      implicit val databaseActor = context.spawn(
        infrastructure.database.DatabaseActor("work-it-db"),
        "DatabaseActor"
      )
      implicit val trainerInfrastructure =
        infrastructure.trainers.InfrastructureHandler(
          infrastructure.trainers.Infrastructure.findTrainer,
          infrastructure.trainers.Infrastructure.findTrainer,
          infrastructure.trainers.Infrastructure.findAllTrainers,
          infrastructure.trainers.Infrastructure.createTrainerHandler,
          infrastructure.trainers.Infrastructure.updateTrainerHandler,
          infrastructure.trainers.Infrastructure.changePasswordTrainerHandler,
          infrastructure.trainers.Infrastructure.deleteTrainerHandler,
          infrastructure.trainers.Infrastructure.preferencesAddedHandler,
          infrastructure.trainers.Infrastructure.preferencesRemovedHandler
        )
      implicit val trainerActor =
        context.spawn(
          ApplicationActor(TrainerApplicationService),
          "TrainerActor"
        )
      implicit val participantInfrastructure =
        infrastructure.participants.InfrastructureHandler(
          infrastructure.participants.Infrastructure.findParticipant,
          infrastructure.participants.Infrastructure.findParticipant,
          infrastructure.participants.Infrastructure.findParticipants,
          infrastructure.participants.Infrastructure.findPreferences,
          infrastructure.participants.Infrastructure.createHandler,
          infrastructure.participants.Infrastructure.updateHandler,
          infrastructure.participants.Infrastructure.preferencesAddedHandler,
          infrastructure.participants.Infrastructure.preferencesRemovedHandler,
          infrastructure.participants.Infrastructure.passwordChangedHandler,
          infrastructure.participants.Infrastructure.deleteHandler,
          infrastructure.participants.Infrastructure.requestIssuedHandler,
          infrastructure.participants.Infrastructure.requestApprovedHandler,
          infrastructure.participants.Infrastructure.requestRejectedHandler
        )
      val participantActor =
        context.spawn(
          ApplicationActor(ParticipantApplicationService),
          "ParticipantActor"
        )
      context.watch(trainerActor)
      context.watch(participantActor)

      val trainerRoutes =
        new TrainerRoutes(trainerActor).trainerRoutes
      val participantRoutes =
        new ParticipantRoutes(participantActor).participantRoutes
      val authRoutes =
        new AuthRoutes(participantActor, trainerActor).authRoutes

      startHttpServer(
        handleRejections(infrastructureHandler) {
          authRoutes ~ participantRoutes ~ trainerRoutes
        }
      )

      Behaviors.empty
    }
    val system =
      ActorSystem[Nothing](rootBehavior, "WorkItSocialDimensionAPIServer")
  }
}
//#main-class
