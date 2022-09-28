package ucab.sqa.workit.reports.infrastructure.db.store.sql

import cats.*
import cats.mtl.Tell
import cats.mtl.syntax.all.*
import cats.syntax.monad.*
import cats.syntax.monadError.*
import cats.syntax.applicativeError.*
import cats.syntax.functor.*
import cats.syntax.flatMap.*
import doobie.implicits.*
import doobie.syntax.all.*
import doobie.util.transactor.Transactor
import cats.effect.kernel.Async
import cats.effect.std.Console
import ucab.sqa.workit.reports.infrastructure.db.store.StoreAction
import doobie.util.Put
import java.util.UUID
import doobie.util.fragment.Fragment

final class SqlStore[F[_]: Async: [F[_]] =>> Tell[F, F[Unit]]](xa: Transactor[F]) extends (StoreAction ~> F):
    private def execute(fragment: Fragment) =
        fragment
        .update
        .run
        .transact(xa)
        .as(())

    private def insert(id: UUID, trainingId: UUID, issuerId: UUID, reason: String) = execute(sql"""
        INSERT INTO reports(id, "trainingId", reason, "issuerId") 
        VALUES (${id.toString}::uuid, ${trainingId.toString}::uuid, $reason, ${issuerId.toString}::uuid)
    """)

    private def delete(id: UUID) = execute(sql"""DELETE FROM "reports" WHERE id = ${id.toString}::uuid""")

    private def find(id: UUID): F[(String, String, String, String)] = 
        sql"""SELECT FROM "reports" WHERE id = ${id.toString}"""
        .query[(String, String, String, String)]
        .option
        .transact(xa)
        .map(_.toRight(Exception(s"Report not stored previously with id $id")))
        .rethrow


    def apply[A](action: StoreAction[A]) = action match
        case StoreAction.StoreReport(id, trainingId, issuerId, reason) => (for
            _ <- insert(id, trainingId, issuerId, reason)
            () <- delete(id).void.tell
        yield ()).attempt
            
        case StoreAction.DeleteReport(id) => (for 
            (rid, trainingId, reason, issuerId) <- find(id)
            () <- delete(id)
            () <- insert(id, UUID.fromString(trainingId), UUID.fromString(issuerId), reason).void.tell
        yield ()).attempt