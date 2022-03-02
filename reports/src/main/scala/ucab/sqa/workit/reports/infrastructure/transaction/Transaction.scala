package ucab.sqa.workit.reports.infrastructure.transaction

import ucab.sqa.workit.reports.infrastructure.transaction.*
import cats.~>
import cats.syntax.all.*
import cats.data.Kleisli
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.Monad

opaque type Transaction[F[_], A] = Kleisli[F, Ref[F, F[Unit]], A]

object Transaction:
    def executeK[F[_]: Sync] = new (([A] =>> Kleisli[F, Ref[F, F[Unit]], A]) ~> F):
        def apply[A](transaction: Kleisli[F, Ref[F, F[Unit]], A]): F[A] = transaction.execute