package ucab.sqa.workit.reports.infrastructure.fitness

import cats.effect.kernel.Resource

trait Service[F[_]] {
    def deleteTraining(id: String): F[_]
}

object Service {
    def resource[F[_]](implicit r: Resource[F, Service[F]]) = r
}