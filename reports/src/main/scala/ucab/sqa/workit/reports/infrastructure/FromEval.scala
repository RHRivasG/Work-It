package ucab.sqa.workit.reports.infrastructure

import cats.Eval

trait FromEval[F[_]] {
    def fromEval[A](eval: Eval[A]): F[A]
}

object FromEval {
    def apply[F[_]](implicit f: FromEval[F]) = f
}