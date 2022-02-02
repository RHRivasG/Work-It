package ucab.sqa.workit.reports

import cats._
import cats.implicits._
import cats.data.EitherT
import fs2.Stream
import ucab.sqa.workit.reports.infrastructure.repository.sql._
import ucab.sqa.workit.reports.infrastructure.errors.ReportError
import ucab.sqa.workit.reports.infrastructure.repository.Repository
import ucab.sqa.workit.reports.application.requests.ReportRequest
import ucab.sqa.workit.reports.application.ReportApplicationService
import ucab.sqa.workit.reports.application.ReportApplicationResult
import ucab.sqa.workit.reports.infrastructure.errors.ReportDomainError
import ucab.sqa.workit.reports.application.actions.ReportApplicationAction
import ucab.sqa.workit.reports.application.actions.GetReport
import ucab.sqa.workit.reports.application.actions.GetReportByTrainer
import ucab.sqa.workit.reports.application.actions.Handle
import ucab.sqa.workit.reports.application.actions.GetAllReports
import ucab.sqa.workit.reports.domain.events.ReportIssuedEvent
import ucab.sqa.workit.reports.domain.events.ReportAcceptedEvent
import ucab.sqa.workit.reports.domain.events.ReportRejectedEvent
import ucab.sqa.workit.reports.domain.events.ReportEvent
import cats.effect.kernel.Async
import cats.effect.std.Queue
import cats.effect.kernel.Resource
import cats.effect.IO
import ucab.sqa.workit.reports.infrastructure.notifications.NotificationHandler
import ucab.sqa.workit.reports.application.models.ReportModel
import ucab.sqa.workit.reports.infrastructure.fitness.Service

package object infrastructure {
    type ReportIO[F[_], A] = EitherT[F, ReportError, A]

    private def streamFromQueue[F[_]: Async, A](q: Queue[F, A]) = 
        Stream.fromQueueUnterminated(q)

    implicit val fromEvalForIO: FromEval[IO] = new FromEval[IO] {
        def fromEval[A](eval: Eval[A]) = IO.eval(eval)
    }

    implicit def fromEvalForEitherT[F[_]: Functor: FromEval, E]: FromEval[EitherT[F, E, *]] = new FromEval[EitherT[F, E, *]] {
        def fromEval[A](eval: Eval[A]) = EitherT.liftF(FromEval[F].fromEval(eval))
    }

    implicit def interpreter[F[_]](notifications: NotificationHandler[F])(fitness: Service[F])(implicit F: FromEval[F], A: Async[F], E: ApplicativeError[F, ReportError], service: ReportApplicationService) = 
        (Repository.resource[F], Resource.eval[F, Queue[F, ReportEvent]] { Queue.bounded(1) }).mapN { (repository, queue) =>
            val queueStream = streamFromQueue(queue)
            val hoistFn = λ[ReportApplicationResult ~> ReportIO[F, *]]{either => 
                val effect = F.fromEval(either.value.map(_.left.map[ReportError](ReportDomainError(_))))
                EitherT(effect)
            }
            val executorFn = λ[ReportApplicationAction ~> ReportIO[F, *]] { action => E.attemptT(action match {
                case GetReport(id) => repository.get(id)
                case GetReportByTrainer(id) => repository.getByTrainer(id)
                case GetAllReports => repository.getAll
                case Handle(ReportIssuedEvent(id, trainingId, reason)) => 
                    repository.create(id, trainingId, reason) >> (repository.getAll.flatMap { rawModels =>
                        val models = rawModels.map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason))
                        notifications.send(models)
                    })
                case Handle(ReportAcceptedEvent(id, trainingId)) => 
                    repository.delete(id) >> Async[F].start(fitness.deleteTraining(trainingId.toString())) >> (repository.getAll.flatMap { rawModels =>
                        val models = rawModels.map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason))
                        notifications.send(models)
                    })
                case Handle(ReportRejectedEvent(id)) => 
                    repository.delete(id) >> (repository.getAll.flatMap { rawModels =>
                        val models = rawModels.map(r => ReportModel(r.id.id.toString, r.trainingId.id.toString, r.reason))
                        notifications.send(models)
                    })
            })}
            val executor = λ[ReportRequest ~> ReportIO[F, *]](service(_).hoist(hoistFn).foldMap(executorFn))
            new InfrastructureService[F] {
                def apply[A](request: ReportRequest[A]): ReportIO[F, A] = executor(request)
                def eventStream = queueStream
            }
        }
}
