package ucab.sqa.workit.reports.infrastructure.notifications.queue

import cats.~>
import cats.effect.std.Queue
import cats.effect.kernel.Async
import cats.implicits.*
import ucab.sqa.workit.reports.application.models.ReportModel
import fs2.Stream
import fs2.concurrent.Topic
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationAction
import cats.effect.kernel.Resource

class NotificationQueue[F[_]: Async](topic: Topic[F, Vector[ReportModel]], queue: Queue[F, Vector[ReportModel]]) 
    extends (NotificationAction ~> F):

    def stream: Stream[F, Vector[ReportModel]] = topic.subscribe(1).concurrently(
        Stream.fromQueueUnterminated(queue).through(topic.publish)
    )

    def apply[A](action: NotificationAction[A]) = action match
        case NotificationAction.Notify(models) => queue.offer(models)

object NotificationQueue:
    def apply[F[_]: Async] = for
        topic <- Resource.eval { Topic[F, Vector[ReportModel]] }
        queue <- Resource.eval { Queue.unbounded[F, Vector[ReportModel]] }
        notificationInterpreter = new NotificationQueue[F](topic, queue)
    yield notificationInterpreter
    