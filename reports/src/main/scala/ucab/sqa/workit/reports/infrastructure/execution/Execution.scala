package ucab.sqa.workit.reports.infrastructure.execution
import ucab.sqa.workit.reports.infrastructure.interpreter.*
import cats.InjectK
import cats.free.Free

enum ExecutionSemantic[F[_], A]:
    case Parallel[F[_], A, B](a: Instruction[F, A], b: Instruction[F, B]) extends ExecutionSemantic[F, (A, B)]
    case Spawn[F[_], A](instruction: Instruction[F, A]) extends ExecutionSemantic[F, Unit]

trait ExecutionSemanticOps[G[_], F[_]]:
    def parallel[A, B](a: Instruction[G, A], b: Instruction[G, B]): Instruction[F, (A, B)]
    def spawn[A](a: Instruction[G, A]): Instruction[F, Unit]

class ExecutionSemanticLanguage[G[_], F[_]](using injector: InjectK[[A] =>> ExecutionSemantic[G, A], F]) extends ExecutionSemanticOps[G, F]:
    def parallel[A, B](a: Instruction[G, A], b: Instruction[G, B]) = 
        Free.liftInject.apply[[A] =>> ExecutionSemantic[G, A], (A, B)](ExecutionSemantic.Parallel(a, b))(injector)
    def spawn[A](a: Instruction[G, A]) = 
        Free.liftInject.apply[[A] =>> ExecutionSemantic[G, A], Unit](ExecutionSemantic.Spawn(a))(injector)