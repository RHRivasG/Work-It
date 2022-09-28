package ucab.sqa.workit.web.infrastructure.log

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.DeadLetter

object DeadLetterActor {
    def apply = Behaviors.receive[DeadLetter] { (ctx, deadLetter) =>
        ctx.log.warn(f"Could not deliver message: ${deadLetter.message} to ${deadLetter.recipient} from ${deadLetter.sender}")
        Behaviors.same
    }
}