package ucab.sqa.workit.web.trainers

import akka.actor.typed.scaladsl.adapter._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.participants.ParticipantCommand
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.Source
import akka.http.scaladsl.model.ws.Message
import akka.actor.typed.scaladsl.AskPattern._
import ucab.sqa.workit.application.participants.GetAllParticipantsQuery
import ucab.sqa.workit.web.Query
import scala.concurrent.Future
import ucab.sqa.workit.application.participants.ParticipantModel
import akka.util.Timeout
import scala.util.Failure
import scala.util.Success
import spray.json._
import akka.actor.typed.Behavior
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.actor.Actor
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import ucab.sqa.workit.web.JsonSupport
import akka.stream.FlowShape
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.javadsl.Merge
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.parboiled2.Parser
import cats.data.OptionT
import ucab.sqa.workit.application.trainers.TrainerModel
import ucab.sqa.workit.application.trainers.TrainerCommand
import ucab.sqa.workit.application.trainers.TrainerQuery
import ucab.sqa.workit.application.trainers.GetTrainersQuery

sealed trait TrainerStreamMessage

final case class UnregisterListener(actor: ActorRef[List[TrainerModel]]) extends TrainerStreamMessage
final case class RegisterListener(actor: ActorRef[List[TrainerModel]]) extends TrainerStreamMessage
final case class ResendTrainers() extends TrainerStreamMessage
private final case class TrainerListings(listings: Receptionist.Listing) extends TrainerStreamMessage
private final case class BroadcastResult(result: Either[Error, List[TrainerModel]]) extends TrainerStreamMessage

object TrainerStreamActor extends JsonSupport {
    def flow(implicit materializer: Materializer, streamingActor: ActorRef[TrainerStreamMessage]) = Flow.fromGraph[Message, Message, Any](GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      import spray.json._

      val (actor, source) = Source.actorRef[List[TrainerModel]](1024, OverflowStrategy.dropNew).preMaterialize
      val effectiveActor = actor.toTyped

      streamingActor ! RegisterListener(effectiveActor)

      val streamSink = Sink.actorRef[TrainerStreamMessage](streamingActor.toClassic, UnregisterListener(effectiveActor))

      val wsInput = builder add Flow[Message].collect {
        case _: Message => ResendTrainers()
      }.take(1)
      val wsOutput = builder add Flow[List[TrainerModel]].map { (participantList: List[TrainerModel]) =>
        TextMessage(participantList.toJson.prettyPrint) 
      }

      wsInput ~> streamSink
      source ~> wsOutput

      FlowShape(wsInput.in, wsOutput.out)
    })

    def apply(state: Seq[ActorRef[List[TrainerModel]]] = Seq())(implicit serviceKey: ServiceKey[Request[TrainerCommand, TrainerQuery, _]]): Behavior[TrainerStreamMessage] = 
        Behaviors.setup[TrainerStreamMessage] { ctx =>
            implicit val system = ctx.system
            implicit val timeout = Timeout.create(
              ctx.system.settings.config.getDuration("work-it-app.ws.ask-timeout")
            )

            val trainerAdapter: ActorRef[Receptionist.Listing] = ctx.messageAdapter(TrainerListings(_))
            system.receptionist ! Receptionist.Subscribe(serviceKey, trainerAdapter)
        
            Behaviors.receiveMessagePartial {
                case TrainerListings(serviceKey.Listing(listings)) => 
                    for {
                        service <- listings.headOption
                        () <- Some(ctx.ask[Request[TrainerCommand, TrainerQuery, _], Either[Error, List[TrainerModel]]](service, Query(GetTrainersQuery(), _)) {
                            case Success(e) => BroadcastResult(e)
                            case Failure(error) => BroadcastResult(Left(new Error(error)))
                        })
                    } yield ()
                    Behaviors.same
                case BroadcastResult(result) => 
                    result match {
                        case Left(value) => system.log.error("Error occured while streaming", value)
                        case Right(value) => state.foreach(_ ! value)
                    }
                    Behaviors.same
                case RegisterListener(actor) =>
                    TrainerStreamActor(state :+ actor)
                case UnregisterListener(actor) => 
                    TrainerStreamActor(state filterNot { _ == actor })
                case ResendTrainers() => 
                    system.receptionist ! Receptionist.Find(serviceKey, trainerAdapter)
                    Behaviors.same
            }
        }
}