package ucab.sqa.workit.aggregator.infrastructure

import cats.effect.IO
import ucab.sqa.workit.aggregator.model.ServiceTable
import ucab.sqa.workit.aggregator.application.AddService
import ucab.sqa.workit.aggregator.application.ServiceAggregatorAction
import ucab.sqa.workit.aggregator.model
import java.util.UUID
import cats.data.EitherT
import ucab.sqa.workit.aggregator.application.CurrentState
import ucab.sqa.workit.aggregator.application.UnsubscribeHost
import ucab.sqa.workit.aggregator.application.SetCurrentState
import cats.data.Kleisli
import ServiceAggregatorExecutor.*
import cats.effect.std.Semaphore
import cats.effect.kernel.Resource
import java.net.URI

private[infrastructure] object ServiceAggregatorInfrastructure {

    private def addService(group: model.Group, service: model.Service): ServiceAggregatorState[Unit] = Kleisli(ref => 
        EitherT.right(ref.update { table =>
            table.addService(group, service)
        })
    )

    private def unsubscribeHost(host: URI): ServiceAggregatorState[Unit] = Kleisli { ref =>
        EitherT.right(ref.update(_.unsubscribeHost(host)))
    }

    private def get(semaphore: Semaphore[IO]): ServiceAggregatorState[ServiceTable] = Kleisli(ref => EitherT.right(for {
        table <- ref.get
        _ <- semaphore.acquire
    } yield table))

    private def set(semaphore: Semaphore[IO], table: ServiceTable): ServiceAggregatorState[Unit] = Kleisli(ref => EitherT.right(for {
        _ <- ref.set(table)
        _ <- semaphore.release
    } yield ()))

    private def semaphore = Resource.eval { Semaphore[IO](1) }

    def apply() = semaphore.map { semaphore => 
        new Interpreter {
            def apply[A](fa: ServiceAggregatorAction[A]): ServiceAggregatorState[A] = fa match {
                case AddService(name, id, host, loadFactor) => for {
                    group <- fromEither(model.Group.of(name))
                    service <- fromEither(model.Service.of(id.toString, host, loadFactor))
                    result <- addService(group, service)
                } yield result
                case UnsubscribeHost(host) => for {
                    service <- fromEither(model.Service.of(UUID.randomUUID.toString, host, 1))
                    host <- Kleisli.liftF(pure(service.host))
                    _ <- unsubscribeHost(host)
                } yield ()
                case CurrentState() => get(semaphore)
                case SetCurrentState(serviceTable) => set(semaphore, serviceTable)
            }
        }
    }
}