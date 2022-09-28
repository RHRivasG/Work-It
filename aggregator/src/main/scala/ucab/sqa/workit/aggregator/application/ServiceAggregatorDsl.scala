package ucab.sqa.workit.aggregator.application

import ucab.sqa.workit.aggregator.model.DomainError
import ucab.sqa.workit.aggregator.model.ServiceTable
import cats.free.FreeT
import java.util.UUID

object ServiceAggregatorDsl {
    type ServiceAggregatorResult[A] = Either[DomainError, A]
    type ServiceAggregatorActionAST[A] = FreeT[ServiceAggregatorAction, ServiceAggregatorResult, A]

    private def lift[A](ast: => ServiceAggregatorAction[A]): ServiceAggregatorActionAST[A] =
        FreeT.liftF(ast)

    def of[A](ast: => Either[DomainError, A]): ServiceAggregatorActionAST[A] =
        FreeT.liftT(ast)

    def current = lift(CurrentState())

    def set(table: ServiceTable) = lift(SetCurrentState(table))

    def addService(group: String, id: UUID, host: String, factor: Int) = lift(AddService(group, id, host, factor))

    def unsubscribe(host: String) = lift(UnsubscribeHost(host))
}