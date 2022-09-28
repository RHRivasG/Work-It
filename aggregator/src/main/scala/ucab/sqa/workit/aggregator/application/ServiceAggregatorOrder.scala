package ucab.sqa.workit.aggregator.application
import ucab.sqa.workit.aggregator.model.Service

sealed trait ServiceAggregatorOrder[A]

final case class AddServiceCommand(group: String, host: String, load: Int) extends ServiceAggregatorOrder[Unit]
final case class Unsubscribe(host: String) extends ServiceAggregatorOrder[Unit]
final case class Next(group: String) extends ServiceAggregatorOrder[Service]