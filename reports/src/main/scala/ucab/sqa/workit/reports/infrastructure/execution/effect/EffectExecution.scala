package ucab.sqa.workit.reports.infrastructure.execution.effect

import cats.~>
import cats.syntax.all.*
import cats.implicits.*
import cats.effect.implicits.*
import ucab.sqa.workit.reports.infrastructure.execution.ExecutionSemantic
import cats.Parallel
import cats.effect.kernel.Async
import scala.concurrent.ExecutionContext

class EffectExecution[F[_], G[_]: Async: Parallel](ec: ExecutionContext, executor: F ~> G) extends (([A] =>> ExecutionSemantic[F, A]) ~> G):
    def apply[A](semantic: ExecutionSemantic[F, A]) = semantic match
        case ExecutionSemantic.Parallel(a, b) => {
            val effectA = a.foldMap(executor)
            val effectB = b.foldMap(executor)

            (effectA, effectB).parMapN { (_, _) }.evalOn(ec)
        }
        case ExecutionSemantic.Spawn(instruction) => {
            instruction.foldMap(executor).startOn(ec).void
        }

object EffectExecution:
    def apply[F[_], G[_]: Async: Parallel](ec: ExecutionContext, executor: F ~> G) = 
        (new EffectExecution(ec, executor)) or executor