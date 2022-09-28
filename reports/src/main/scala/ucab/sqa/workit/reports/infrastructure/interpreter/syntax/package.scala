package ucab.sqa.workit.reports.infrastructure.interpreter

import cats.data.EitherT
import cats.free.Free
import cats.InjectK

package object syntax:
    extension [A](e: InfrastructureInstruction[A])
        def dsl: InfrastructureLanguage[A] = EitherT.right(e.inject)

    extension [A](e: InfrastructureInstruction[Either[Throwable, A]])
        def andFail: InfrastructureLanguage[A] = EitherT(e.inject)

    extension [A](e: Either[Throwable, A])
        def fail: InfrastructureLanguage[A] = EitherT.fromEither(e)
