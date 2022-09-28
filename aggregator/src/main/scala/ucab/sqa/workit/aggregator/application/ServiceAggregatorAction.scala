package ucab.sqa.workit.aggregator.application

import ucab.sqa.workit.aggregator.model.ServiceTable
import java.util.UUID

sealed trait ServiceAggregatorAction[A]

final case class AddService(group: String, id: UUID, host: String, loadFactor: Int) extends ServiceAggregatorAction[Unit]
final case class UnsubscribeHost(host: String) extends ServiceAggregatorAction[Unit]
final case class SetCurrentState(serviceTable: ServiceTable) extends ServiceAggregatorAction[Unit]
final case class CurrentState() extends ServiceAggregatorAction[ServiceTable]