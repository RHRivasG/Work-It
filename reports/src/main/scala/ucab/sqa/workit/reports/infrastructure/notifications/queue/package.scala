package ucab.sqa.workit.reports.infrastructure.notifications

import fs2.Stream
import cats.effect.std.Queue
import cats.effect.kernel.Concurrent
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.effect.kernel.Resource
import cats.effect.std
import fs2.concurrent.Topic

package object queue {
    implicit def pubSubNotificationHandler[F[_]: Concurrent: std.Console] = for {
      queue <- Resource.eval { Queue.unbounded[F, Vector[ReportModel]] }
      pStream = Stream.fromQueueUnterminated(queue, Int.MaxValue)
      topic <- Resource.eval { Topic[F, Vector[ReportModel]] }
    } yield new NotificationHandler[F] {
      override def processingStream: Stream[F, Unit] = 
        pStream
        .flatMap(Stream.emit)
        .through(topic.publish)
      override def stream: Stream[F,Vector[ReportModel]] = topic.subscribe(Int.MaxValue)
      override def send(models: Vector[ReportModel]): F[Unit] = queue.offer(models)
    }
}
