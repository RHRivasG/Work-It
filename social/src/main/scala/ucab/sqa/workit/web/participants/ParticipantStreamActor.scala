package ucab.sqa.workit.web.participants

import akka.actor.typed.ActorRef
import ucab.sqa.workit.web.Request
import ucab.sqa.workit.application.participants.ParticipantQuery
import ucab.sqa.workit.application.participants.ParticipantCommand
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.Message
import ucab.sqa.workit.application.participants.GetAllParticipantsQuery
import ucab.sqa.workit.web.Query
import ucab.sqa.workit.application.participants.ParticipantModel
import akka.util.Timeout
import scala.util.Failure
import scala.util.Success
import akka.actor.typed.Behavior
import akka.stream.Materializer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import ucab.sqa.workit.web.JsonSupport
import akka.stream.FlowShape
import akka.http.scaladsl.model.ws.TextMessage
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.stream.typed.scaladsl.ActorSource
import akka.stream.typed.scaladsl.ActorSink

sealed trait ParticipantStreamMessage

final case class UnregisterListener(actor: ActorRef[List[ParticipantModel]]) extends ParticipantStreamMessage
final case class RegisterListener(actor: ActorRef[List[ParticipantModel]]) extends ParticipantStreamMessage
final case class ResendParticipants() extends ParticipantStreamMessage
private final case class ParticipantListings(listings: Receptionist.Listing) extends ParticipantStreamMessage
private final case class BroadcastResult(result: Either[Error, List[ParticipantModel]]) extends ParticipantStreamMessage

object ParticipantStreamActor extends JsonSupport {
    def flow(implicit materializer: Materializer, streamingActor: ActorRef[ParticipantStreamMessage]) = Flow.fromGraph[Message, Message, Any](GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      import spray.json._

      val (actor, source) = ActorSource.actorRef[List[ParticipantModel]](
            completionMatcher = PartialFunction.empty,
            failureMatcher = PartialFunction.empty, 
            bufferSize = 1024, 
            overflowStrategy = OverflowStrategy.dropTail
        )
        .preMaterialize()

      streamingActor ! RegisterListener(actor)

      val streamSink = ActorSink.actorRef[ParticipantStreamMessage](
        streamingActor, 
        UnregisterListener(actor),
        onFailureMessage = _ => UnregisterListener(actor)
      )

      val wsInput = builder add Flow[Message].collect {
        case _: Message => ResendParticipants()
      }.take(1)
      val wsOutput = builder add Flow[List[ParticipantModel]].map { (participantList: List[ParticipantModel]) =>
        println(f"Sending to stream ${participantList.toJson.prettyPrint}")
        TextMessage(participantList.toJson.prettyPrint) 
      }

      wsInput ~> streamSink
      source ~> wsOutput

      FlowShape(wsInput.in, wsOutput.out)
    })

    def apply(state: Seq[ActorRef[List[ParticipantModel]]] = Seq())(implicit serviceKey: ServiceKey[Request[ParticipantCommand, ParticipantQuery, _]]): Behavior[ParticipantStreamMessage] = 
        Behaviors.setup[ParticipantStreamMessage] { ctx =>
            implicit val system = ctx.system
            implicit val timeout = Timeout.create(
              ctx.system.settings.config.getDuration("work-it-app.ws.ask-timeout")
            )

            val participantAdapter: ActorRef[Receptionist.Listing] = ctx.messageAdapter(ParticipantListings(_))
            system.receptionist ! Receptionist.Subscribe(serviceKey, participantAdapter)
        
            Behaviors.receiveMessagePartial {
                case ParticipantListings(serviceKey.Listing(listings)) => 
                    for {
                        service <- listings.headOption
                        () <- Some(ctx.ask[Request[ParticipantCommand, ParticipantQuery, _], Either[Error, List[ParticipantModel]]](service, Query(GetAllParticipantsQuery(), _)) {
                            case Success(e) => {
                                system.log.info(f"Broadcasting to participants: $e")
                                BroadcastResult(e)
                            }
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
                    ParticipantStreamActor(state :+ actor)
                case UnregisterListener(actor) => 
                    ParticipantStreamActor(state filterNot { _ == actor })
                case ResendParticipants() => 
                    system.receptionist ! Receptionist.Find(serviceKey, participantAdapter)
                    Behaviors.same
            }
        }
}