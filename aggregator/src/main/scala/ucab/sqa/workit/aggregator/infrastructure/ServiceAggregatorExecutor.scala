package ucab.sqa.workit.aggregator.infrastructure

import cats._
import cats.implicits._
import cats.data.EitherT
import cats.data.StateT
import cats.effect.IO
import ucab.sqa.workit.aggregator.model.DomainError
import ucab.sqa.workit.aggregator.model.ServiceTable
import ucab.sqa.workit.aggregator.application.ServiceAggregatorAction
import ucab.sqa.workit.aggregator.application.AddService
import ucab.sqa.workit.aggregator.application.CurrentState
import ucab.sqa.workit.aggregator.application.SetCurrentState
import ucab.sqa.workit.aggregator.application.ServiceAggregatorOrder
import ucab.sqa.workit.aggregator.application.ServiceAggregatorDsl._
import cats.effect.kernel.Resource
import cats.effect.kernel.Ref
import cats.data.Kleisli
import cats.effect.std.Semaphore

object ServiceAggregatorExecutor {
    private[infrastructure] type InnerState = Ref[IO, ServiceTable]
    type FallibleIO[A] = EitherT[IO, DomainError, A]
    type ServiceAggregatorState[A] = Kleisli[FallibleIO, InnerState, A]
    type Interpreter = ServiceAggregatorAction ~> ServiceAggregatorState
    type Executor = ServiceAggregatorOrder ~> ServiceAggregatorState
    type Service = ServiceAggregatorOrder ~> FallibleIO

    private[infrastructure] def liftK[A](either: => IO[Either[DomainError, A]]): ServiceAggregatorState[A] = Kleisli.liftF(liftF(either))

    private[infrastructure] def fromEither[A](either: => Either[DomainError, A]): ServiceAggregatorState[A] = Kleisli.liftF(EitherT.fromEither(either))

    private[infrastructure] def liftF[A](either: => IO[Either[DomainError, A]]): FallibleIO[A] = EitherT(either)

    private[infrastructure] def lift[A](either: => Either[DomainError, A]): FallibleIO[A] = EitherT.fromEither(either)

    private[infrastructure] def pure[A](x: => A): FallibleIO[A] = EitherT.pure(x)

    private val hoister = new (ServiceAggregatorResult ~> ServiceAggregatorState) {
      override def apply[A](fa: ServiceAggregatorResult[A]): ServiceAggregatorState[A] = fromEither(fa)
    }
    def apply()(implicit useCaseBuilder: ServiceAggregatorOrder ~> ServiceAggregatorActionAST) = for {
      state <- ServiceAggregatorDatabase()
      infrastructure <-ServiceAggregatorInfrastructure()
    } yield new (Service) {

      private val executor = new (ServiceAggregatorAction ~> ServiceAggregatorState) {
        override def apply[A](fa: ServiceAggregatorAction[A]): ServiceAggregatorState[A] = infrastructure(fa)
      }

      private val transform = new (ServiceAggregatorActionAST ~> ServiceAggregatorState) {
        override def apply[A](fa: ServiceAggregatorActionAST[A]): ServiceAggregatorState[A] = fa.hoist(hoister).foldMap(executor)
      }

      private val service = useCaseBuilder.andThen(transform)

      def apply[A](fa: ServiceAggregatorOrder[A]): FallibleIO[A] = 
          service(fa)(state)
    }
}