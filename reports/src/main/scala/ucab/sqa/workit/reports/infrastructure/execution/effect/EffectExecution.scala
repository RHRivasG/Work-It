package ucab.sqa.workit.reports.infrastructure.execution.effect

import cats.~>
import cats.syntax.all.*
import cats.implicits.*
import cats.instances.tuple.*
import cats.effect.implicits.*
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemantic
import ucab.sqa.workit.reports.infrastructure.interpreter.InfrastructureAction
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.Parallel
import cats.effect.kernel.Async
import scala.concurrent.ExecutionContext
import cats.free.Free
import cats.data.EitherK
import cats.Monad
import scala.annotation.nowarn

type CompleteLang[F[_]] = [A] =>> EitherK[ExecutionSemantic, F, A]
type Extract[F[_]] = [A] =>> A match
    case Free[f, a] => F[a]

class EffectExecution[F[_], G[_]: Async: Parallel](ec: ExecutionContext, executor: F ~> G) extends (ExecutionSemantic ~> G):
    extension[T <: Tuple, I <: Tuple.InverseMap[T, [A] =>> Free[CompleteLang[F], A]]](tuple: T)
        def traverse[F[_]](nt: CompleteLang[F] ~> G): G[I] = tuple match
            case EmptyTuple => EmptyTuple.pure.asInstanceOf[G[I]]
            case h *: t => 
                (h.asInstanceOf[Free[CompleteLang[F], _]].foldMap(nt), t.traverse(nt))
                .parMapN(_ *: _)
                .asInstanceOf[G[I]]

    @nowarn
    def apply[A](semantic: ExecutionSemantic[A]) = semantic match
        case parallel: ExecutionSemantic.Parallel[CompleteLang[F], ?] => parallel
            .actions
            .traverse(this or executor)
            .evalOn(ec)
        case ExecutionSemantic.Spawn(instruction: Free[CompleteLang[F], ?]) => instruction
            .foldMap(this or executor)
            .startOn(ec)
            .void

object EffectExecution:
    def apply[F[_], G[_]: Async: Parallel](ec: ExecutionContext, executor: F  ~> G) = 
        (new EffectExecution(ec, executor)) or executor