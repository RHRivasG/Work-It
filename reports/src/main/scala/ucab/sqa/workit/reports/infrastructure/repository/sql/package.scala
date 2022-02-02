package ucab.sqa.workit.reports.infrastructure.repository
import doobie._
import doobie.implicits._
import cats.syntax.all._
import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import java.util.UUID
import ucab.sqa.workit.reports.infrastructure.errors.ReportError
import ucab.sqa.workit.reports.domain.Report
import ucab.sqa.workit.reports.domain.errors.ReportNotFoundError
import ucab.sqa.workit.reports.infrastructure.errors.ReportInfrastructureError
import ucab.sqa.workit.reports.infrastructure.errors.ReportDomainError
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import cats.ApplicativeError
import ucab.sqa.workit.reports.domain.errors.InvalidUUIDError

package object sql {
    implicit def doobieRepository[F[_]](implicit E: ApplicativeError[F, ReportError], A: Async[F]) = for {
        config <- Resource.eval(A.flatMap(A.blocking {
            ConfigSource.default.load[Configuration]
        }) {
            _.fold[F[Configuration]](
                f => E.raiseError(ReportInfrastructureError(new Exception(f.prettyPrint(2)))),
                E.pure
            )
        })
        transactor <- Resource.eval(A.blocking {
            Transactor.fromDriverManager[F](
                config.db.driver,
                config.db.url,
                config.db.user,
                config.db.password
            )
        })
    } yield new Repository[F] {
        private def handle[A](fa: => F[A]) = A.handleErrorWith(fa) { err =>
            E.raiseError(ReportInfrastructureError(err))
        }

        private def toReport(tuple: (String, String, String)) = tuple match {
            case (id, trainingId, reason) => A.flatMap(A.delay(UUID.fromString(id))) { id =>
                A.map(A.delay(UUID.fromString(trainingId))) { trainingId =>
                    Report(id, trainingId, reason)
                }
            }
        }

        override def get(id: String): F[Report] = handle {
            A.handleErrorWith(A.blocking { UUID.fromString(id) }) { err =>
                E.raiseError(ReportDomainError(InvalidUUIDError(err)))
            } >>
            A.flatMap(
              sql"""SELECT * FROM reports WHERE "id" = $id::uuid"""
              .query[(String, String, String)]
              .to[Seq]
              .transact(transactor)
            ) { _.headOption match {
              case None => E.raiseError(ReportDomainError(ReportNotFoundError(id)))
              case Some(entity) => toReport(entity)
            } }
        }

        override def getByTrainer(id: String): F[Report] = handle {
            A.handleErrorWith(A.blocking { UUID.fromString(id) }) { err =>
                E.raiseError(ReportDomainError(InvalidUUIDError(err)))
            } >>
            A.flatMap(
              sql"""SELECT * FROM reports WHERE "trainingId" = $id::uuid"""
              .query[(String, String, String)]
              .to[Seq]
              .transact(transactor)
            ) { _.headOption match {
              case None => E.raiseError(ReportDomainError(ReportNotFoundError(id)))
              case Some(entity) => toReport(entity)
            } }
        }

      override def getAll: F[Vector[Report]] = handle {
          A.flatMap(
              sql"SELECT * FROM reports"
              .query[(String, String, String)]
              .to[Vector]
              .transact(transactor)
          ) {
              _.traverse[F, Report](toReport)(E)
          }
      }

      override def create(id: UUID, trainingId: UUID, reason: String): F[Unit] = handle {
          A.as(sql"""INSERT INTO reports(id, "trainingId", reason) VALUES(${id.toString()}::uuid, ${trainingId.toString()}::uuid, $reason)"""
            .update
            .run
            .transact(transactor),
            ()
          )
      }

      override def update(id: UUID, trainingId: UUID, reason: String): F[Unit] = handle {
          A.as(
            sql"""UPDATE SET "trainingId" = ${id.toString()} reason = $reason FROM reports WHERE id = ${id.toString}"""
            .update
            .run
            .transact(transactor),
            ()
        )
      }

      override def delete(id: UUID): F[Unit] = handle {
          A.as(
            sql"DELETE FROM reports WHERE id = ${id.toString}::uuid"
            .update
            .run
            .transact(transactor),
            ()
        )
      }
    }
}
