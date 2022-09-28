package ucab.sqa.workit.aggregator.application

import cats.*
import ucab.sqa.workit.aggregator.model.Group
import ServiceAggregatorDsl.*
import java.util.UUID

object ServiceAggregatorUseCaseBuilder {
    implicit val builder: ServiceAggregatorOrder ~> ServiceAggregatorActionAST = new (ServiceAggregatorOrder ~> ServiceAggregatorActionAST) {
      override def apply[A](fa: ServiceAggregatorOrder[A]): ServiceAggregatorActionAST[A] = fa match {
          case AddServiceCommand(group, host, load) => addService(group, UUID.randomUUID(), host, load)
          case Unsubscribe(host) => unsubscribe(host)
          case Next(group) => for {
              state <- current
              group <- of(Group.of(group))
              result <- of(state.nextService(group))
              _ <- set(result._2)
          } yield result._1
      }
    }
    def apply = implicitly[ServiceAggregatorOrder ~> ServiceAggregatorActionAST]
}