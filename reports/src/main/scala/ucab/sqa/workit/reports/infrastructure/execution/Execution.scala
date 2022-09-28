package ucab.sqa.workit.reports.infrastructure.execution

import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.InjectK
import cats.free.Free
import cats.data.EitherK
import shapeless.HList

enum ExecutionSemantic[A]:
    case Parallel[F[_], I <: Tuple](actions: I) 
        extends ExecutionSemantic[Tuple.InverseMap[I, [A] =>> Instruction[F, A]]]
    case Spawn[F[_], A](instruction: Instruction[F, A]) extends ExecutionSemantic[Unit]

trait ExecutionSemanticOps[F[_]]:
    def parallel[I <: Tuple](actions: I)(using evidence: Tuple.IsMappedBy[[A] =>> Instruction[F, A]][I]): Instruction[F, Tuple.InverseMap[I, [A] =>> Instruction[F, A]]]
    def spawn[A](a: Instruction[F, A]): Instruction[F, Unit]

class ExecutionSemanticLanguage[F[_]](using injector: InjectK[ExecutionSemantic, F]) extends ExecutionSemanticOps[F]:
    def parallel[I <: Tuple](actions: I)(using evidence: Tuple.IsMappedBy[[A] =>> Instruction[F, A]][I]): Instruction[F, Tuple.InverseMap[I, [A] =>> Instruction[F, A]]] =
        Free.liftInject(ExecutionSemantic.Parallel[F, I](actions))(injector)
    def spawn[A](a: Instruction[F, A]) = 
        Free.liftInject.apply[ExecutionSemantic, Unit](ExecutionSemantic.Spawn(a))(injector)
    
    extension [I <: Tuple](tuple: I)
        (using evidence: Tuple.IsMappedBy[[A] =>> Instruction[F, A]][I])
        def concurrent = parallel(tuple)

    extension [A](f: Free[F, A])
        def background = spawn(f)