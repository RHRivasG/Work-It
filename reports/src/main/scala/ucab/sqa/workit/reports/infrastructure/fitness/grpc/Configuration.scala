package ucab.sqa.workit.reports.infrastructure.fitness.grpc

case class AggregatorConfiguration(port: Int, host: String)

case class Configuration(aggregator: AggregatorConfiguration)