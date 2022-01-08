package ucab.sqa.workit.aggregator

import ucab.sqa.workit.probobuf.aggregator.ServiceAggregatorFs2Grpc
import cats.effect.IO

object ServiceAggregator extends ServiceAggregatorFs2Grpc[IO, ?] {
}
