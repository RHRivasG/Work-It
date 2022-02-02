package ucab.sqa.workit.application

import cats._

trait ApplicationService[F[_], C, Q[_]] {
  def execute(command: C): ApplicationServiceAction[F, Unit]
  def query[A](command: Q[A]): ApplicationServiceAction[F, A]
}

trait ApplicationServiceAction[F[_], A] {
  def run[G[_]](executor: F ~> G)(implicit m: Monad[G]): G[Either[Error, A]]
}
