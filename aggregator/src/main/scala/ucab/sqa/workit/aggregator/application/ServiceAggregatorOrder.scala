package ucab.sqa.workit.aggregator.application

import cats._
import cats.syntax.apply
import _root_.ucab.sqa.workit.aggregator.model.Service

sealed trait ServiceAggregatorOrder[A]

final case class AddServiceCommand(group: String, host: String, load: Int) extends ServiceAggregatorOrder[Unit]
final case class RemoveServiceCommand(group: String, id: String) extends ServiceAggregatorOrder[Unit]
final case class Next(group: String) extends ServiceAggregatorOrder[Service]