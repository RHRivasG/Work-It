package ucab.sqa.workit.reports.infrastructure.db

import cats.effect.kernel.Async
import cats.mtl.Tell
import cats.effect.kernel.Resource
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import ucab.sqa.workit.reports.infrastructure.db.configuration.Configuration
import doobie.hikari.HikariTransactor
import ucab.sqa.workit.reports.infrastructure.db.store.sql.SqlStore
import ucab.sqa.workit.reports.infrastructure.db.lookup.sql.SqlLookup

object SqlDatabase:
    def apply[F[_]: Async: [F[_]] =>> Tell[F, F[Unit]]] = for
      qce <- Resource.eval { Async[F].delay { ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(32)) } }
      //// Configurations
      dbConfig <- Configuration[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        dbConfig.driver,
        dbConfig.url,
        dbConfig.username,
        dbConfig.password,
        qce
      )
      lookupInterpreter = SqlLookup[F](transactor)
      storeInterpreter = SqlStore[F](transactor)
    yield (lookupInterpreter, storeInterpreter)
