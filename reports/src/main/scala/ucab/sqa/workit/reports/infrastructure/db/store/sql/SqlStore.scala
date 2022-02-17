package ucab.sqa.workit.reports.infrastructure.db.store.sql

import cats.*
import cats.implicits.*
import doobie.implicits.*
import doobie.syntax.all.*
import doobie.util.transactor.Transactor
import cats.effect.kernel.Async
import cats.effect.std.Console
import ucab.sqa.workit.reports.infrastructure.db.store.StoreAction
import doobie.util.Put
import java.util.UUID
import doobie.util.fragment.Fragment

final class SqlStore[F[_]: Async](xa: Transactor[F]) extends (StoreAction ~> F):
    private def execute(fragment: Fragment) =
        fragment
        .update
        .run
        .transact(xa)
        .onError { err => Async[F].delay(println(f"Occured error $err")) }
        .onSqlException { Async[F].delay(println(f"Occured error sql exception"))}
        .as(())
        .attempt

    def apply[A](action: StoreAction[A]) = action match
        case StoreAction.StoreReport(id, trainingId, issuerId, reason) =>
            execute(sql"""
                INSERT INTO reports(id, "trainingId", reason, "issuerId") 
                VALUES (${id.toString}::uuid, ${trainingId.toString}::uuid, $reason, ${issuerId.toString}::uuid)
            """)
        case StoreAction.DeleteReport(id) =>
            execute(sql"""DELETE FROM "reports" WHERE id = ${id.toString}""")