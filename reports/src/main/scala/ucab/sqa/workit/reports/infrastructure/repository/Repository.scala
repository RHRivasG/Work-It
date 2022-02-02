package ucab.sqa.workit.reports.infrastructure.repository

import ucab.sqa.workit.reports.domain.Report
import java.util.UUID
import cats.effect.kernel.Resource

trait Repository[F[_]] {
    def get(id: String): F[Report]
    def getByTrainer(id: String): F[Report]
    def getAll: F[Vector[Report]]
    def create(id: UUID, trainingId: UUID, reason: String): F[Unit]
    def update(id: UUID, trainingId: UUID, reason: String): F[Unit]
    def delete(id: UUID): F[Unit]
}

object Repository {
    def apply[F[_]](implicit repo: Repository[F]) = repo
    def resource[F[_]](implicit resource: Resource[F, Repository[F]]) = resource
}