package ucab.sqa.workit.web

import java.util.UUID
import akka.actor._
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
import ucab.sqa.workit.web.helpers.routes._
import ucab.sqa.workit.domain.participants.ParticipantEvent
import ucab.sqa.workit.web.participants.ParticipantRoutes
import ucab.sqa.workit.web.participants.ParticipantStreamActor
import ucab.sqa.workit.domain.participants.ParticipantDeletedEvent
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantId
import ucab.sqa.workit.application.participants.ParticipantApplicationService
import ucab.sqa.workit.application.trainers.TrainerApplicationService
import ucab.sqa.workit.web.infrastructure
import ucab.sqa.workit.web.trainers.TrainerRoutes
import ucab.sqa.workit.web.auth.AuthRoutes
import akka.http.scaladsl.server.MethodRejection
import akka.http.scaladsl.server.TransformationRejection
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.headers._
import akka.actor.DeadLetter
import akka.actor.typed.eventstream.EventStream
import akka.actor.typed.receptionist.ServiceKey
import ucab.sqa.workit.web.infrastructure.services.FitnessDimensionService
import ucab.sqa.workit.web.infrastructure.services.AuthDimensionService
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.participants.ParticipantCommand
import ucab.sqa.workit.web.participants.ParticipantStreamMessage
import akka.actor.typed.receptionist.Receptionist
import ucab.sqa.workit.application.trainers.TrainerCommand
import ucab.sqa.workit.application.trainers.TrainerQuery
import ucab.sqa.workit.web.trainers.TrainerStreamActor
import ucab.sqa.workit.web.profile.ProfileRoutes
import akka.discovery.Discovery

object App {
  implicit val participantServiceKey: ServiceKey[Request[ParticipantCommand, ParticipantQuery, _]] = ServiceKey("Participants")
  implicit val trainerServiceKey: ServiceKey[Request[TrainerCommand, TrainerQuery, _]] = ServiceKey("Trainers")

  private def corsHeaders = Seq(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization",
      "Content-Type", "X-Requested-With")
  )

  private def startHttpServer(routes: Route)(implicit
      system: ActorSystem[_]
  ): Unit = {
    import system.executionContext

    val futureBinding = Http()
      .newServerAt("0.0.0.0", 5000)
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
        .newBuilder
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

      val deadLetterActor = context.system.systemActorOf(
        infrastructure.log.DeadLetterActor.apply,
        "DeadLetterActor"
      )

      val discovery = Discovery(system).discovery

      implicit val authActor = context.spawn(
        AuthDimensionService(discovery),
        "AuthDimensionService"
      )

      implicit val fitnessActor = context.spawn(
        FitnessDimensionService(discovery),
        "FitnessDimensionActor"
      )

      implicit val authorityActor = context.spawn(
        auth.AuthorityActor(system.settings.config.getString("work-it-app.secret.key")),
        "AuthorityActor"
      )

      implicit val databaseActor = context.spawn(
        infrastructure.database.DatabaseActor("work-it-db"),
        "DatabaseActor"
      )
      
      implicit val participantStreamActor = 
        context.spawn(
          ParticipantStreamActor(),
          "ParticipantStreamActor"
        )
      
      implicit val trainerStreamActor = 
        context.spawn(
          TrainerStreamActor(),
          "TrainerStreamActor"
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

      implicit val participantActor =
        context.spawn(
          ApplicationActor(ParticipantApplicationService),
          "ParticipantActor"
        )

      system.receptionist ! Receptionist.Register(participantServiceKey, participantActor)
      system.receptionist ! Receptionist.Register(trainerServiceKey, trainerActor)

      context.watch(trainerStreamActor)
      context.watch(participantStreamActor)
      context.watch(databaseActor)
      context.watch(trainerActor)
      context.watch(participantActor)
      context.watch(authorityActor)
      context.watch(deadLetterActor)

      val profileRoutes =
        ProfileRoutes(participantActor, trainerActor)

      val trainerRoutes =
        new TrainerRoutes(trainerActor).trainerRoutes

      val participantRoutes =
        new ParticipantRoutes(participantActor).participantRoutes

      val authRoutes =
        new AuthRoutes(participantActor, trainerActor, authorityActor).authRoutes

      system.eventStream ! EventStream.Subscribe[DeadLetter](deadLetterActor)

      startHttpServer(
        (cors(CorsSettings(system.settings.config.getConfig("work-it-app"))) & helpers.auth.delegateAuthenticationEntryPoint) {
          Route.seal(
            handleRejections(infrastructureHandler) {
              authRoutes ~ profileRoutes ~ participantRoutes ~ trainerRoutes
            }
          )
        }
      )

      Behaviors.empty
    }
    val system =
      ActorSystem[Nothing](rootBehavior, "WorkItSocialDimensionAPIServer")
  }
}
