package ucab.sqa.workit.reports.infrastructure.db

import cats.effect.kernel.Sync
import cats.implicits.*
import cats.syntax.all.*
import pureconfig.ConfigReader
import pureconfig.ConfigSource

package object configuration {
    given ConfigReader[Configuration] = ConfigReader.forProduct4("driver", "url", "username", "password")(Configuration.apply)

    private def loadConfig[F[_]: Sync] = Sync[F].delay(ConfigSource.default.at("db").load[Configuration])

    def getConfiguration[F[_]: Sync] = for
        result <- loadConfig
        configuration <- result match
            case Right(value) => value.pure
            case Left(e) => new Error(s"Error loading db config: $e").raiseError
    yield configuration
}