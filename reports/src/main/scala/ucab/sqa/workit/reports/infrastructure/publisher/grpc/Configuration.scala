package ucab.sqa.workit.reports.infrastructure.publisher.grpc

import cats.syntax.all.*
import cats.effect.kernel.Async
import pureconfig.ConfigReader
import cats.effect.kernel.Resource
import pureconfig.ConfigSource

final case class Configuration(host: String, port: Int)

given ConfigReader[Configuration] = ConfigReader.forProduct2("host", "port")(Configuration.apply)

object Configuration:
    def apply[F[_]: Async] = Resource.eval { 
      ConfigSource.default.at("aggregator").load[Configuration] match
        case Right(conf) => conf.pure
        case Left(e) => Exception(f"Occured error while readind grpc configuration $e").raiseError
    }