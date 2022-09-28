package ucab.sqa.workit.reports.infrastructure

import cats.~>
import cats.Monad
import cats.effect.implicits.*
import cats.syntax.all.*
import cats.data.Kleisli
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.effect.kernel.Async
import cats.mtl.Tell
import cats.Functor
import scala.deriving.Mirror

package object transaction:
    extension [F[_]: Sync, A](transaction: Kleisli[F, Ref[F, F[Unit]], A])
        private def runWith(consume: Ref[F, F[Unit]] => F[Unit]) = for
            state <- Ref.of(().pure)
            resultT <- transaction(state).attempt
            result <- resultT match 
                case Left(e) => consume(state) >> e.raiseError
                case Right(v) => v.pure
        yield result
        def execute = transaction.runWith(_.get >>= identity)
        def uncompensated = transaction.runWith(_ => Monad[F].unit)


    implicit def tellForKleisli[F[_]: Sync]: Tell[[A] =>> Kleisli[F, Ref[F, F[Unit]], A], Kleisli[F, Ref[F, F[Unit]], Unit]] =
        new Tell[[A] =>> Kleisli[F, Ref[F, F[Unit]], A], Kleisli[F, Ref[F, F[Unit]], Unit]]:
            def functor = summon[Functor[[A] =>> Kleisli[F, Ref[F, F[Unit]], A]]]
            def tell(compensate: Kleisli[F, Ref[F, F[Unit]], Unit]) = Kleisli { (ctx: Ref[F, F[Unit]]) => 
                ctx.update { _ >> compensate.uncompensated }
            }