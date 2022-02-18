package ucab.sqa.workit.reports.infrastructure.db.configuration
import cats.effect.kernel.Sync
import cats.implicits.*
import cats.syntax.all.*
import pureconfig.ConfigReader
import pureconfig.ConfigSource
import cats.effect.kernel.Resource

final case class Configuration(driver: String, url: String, username: String, password: String)

given ConfigReader[Configuration] = ConfigReader.forProduct4("driver", "url", "username", "password")(Configuration.apply)

object Configuration:
    private def loadConfig[F[_]: Sync] = Sync[F].delay(ConfigSource.default.at("db").load[Configuration])

    def apply[F[_]: Sync] = Resource.eval {
        for
            result <- loadConfig
            configuration <- result match
                case Right(value) => value.pure
                case Left(e) => new Error(s"Error loading db config: $e").raiseError
        yield configuration
    }