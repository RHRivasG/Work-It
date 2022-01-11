package ucab.sqa.workit.aggregator.application

import cats._
import _root_.ucab.sqa.workit.aggregator.model.Group
import ServiceAggregatorDsl._
import java.util.UUID

object ServiceAggregatorUseCaseBuilder {
    implicit val builder = new (ServiceAggregatorOrder ~> ServiceAggregatorActionAST) {
      override def apply[A](fa: ServiceAggregatorOrder[A]): ServiceAggregatorActionAST[A] = fa match {
          case AddServiceCommand(group, host, load) => addService(group, UUID.randomUUID(), host, load)
          case RemoveServiceCommand(group, id) => removeService(group, id)
          case Next(group) => for {
              state <- current
              group <- of(Group.of(group))
              (service, state) <- of(state.nextService(group))
              () <- set(state)
          } yield service
      }
    }

    def apply = implicitly[ServiceAggregatorOrder ~> ServiceAggregatorActionAST]
}