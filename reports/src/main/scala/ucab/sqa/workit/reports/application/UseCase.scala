package ucab.sqa.workit.reports.application

import cats.~>
import cats.syntax.all.*
import ucab.sqa.workit.reports.application.dsl.ReportsDSL as DSL
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.Monad
import cats.MonadError
import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.data.NonEmptyList

type ErrorHandler = NonEmptyList[DomainError] => Throwable
type Compiler[F[_]] = ReportInput ~> F
type Interpreter[G[_], F[_]] = G ~> F

sealed trait UseCase[G[_], F[_]]:
    def report(id: String): F[ReportModel]
    def reports: F[Vector[ReportModel]]
    def reportsOfTraining(id: String): F[Vector[ReportModel]]
    def issueReport(issuer: String, training: String, reason: String): F[Unit]
    def acceptReport(id: String): F[Unit]
    def rejectReport(id: String): F[Unit]
    def reportStream: G[Vector[ReportModel]]

    def mapK[T[_]](f: F ~> T) = {
        val last = this
        new UseCase[G, T]:
            def report(id: String) = f(last.report(id))
            def reports = f(last.reports)
            def reportsOfTraining(id: String) = f(last.reportsOfTraining(id))
            def issueReport(issuer: String, training: String, reason: String) = f(last.issueReport(issuer, training, reason))
            def acceptReport(id: String) = f(last.acceptReport(id))
            def rejectReport(id: String) = f(last.rejectReport(id))
            def reportStream = last.reportStream
    }

object UseCase:
    def apply[G[_], F[_]](using uc: UseCase[G, F]) = uc
    def build[K[_], G[_], F[_]: [F[_]] =>> MonadError[F, Throwable]](
        stream: K[Vector[ReportModel]],
        errorHandler: ErrorHandler,
        compiler: Compiler[G],
        interpreter: Interpreter[G, F]
    ) = new UseCase[K, F]:
        def executor = new (ReportAction ~> F):
            def apply[A](action: ReportAction[A]) = action
              .value
              .compile(compiler)
              .foldMap(interpreter)
              .flatMap { 
                case Right(res) => res.pure
                case Left(err) => errorHandler(err).raiseError }

        def report(id: String): F[ReportModel] = executor(DSL.report(id))
        def reports: F[Vector[ReportModel]] = executor(DSL.reports)
        def reportsOfTraining(id: String): F[Vector[ReportModel]] = executor(DSL.reportsOfTraining(id))
        def issueReport(issuer: String, training: String, reason: String): F[Unit] = executor(DSL.issueReport(issuer, training, reason))
        def acceptReport(id: String): F[Unit] = executor(DSL.acceptReport(id))
        def rejectReport(id: String): F[Unit] = executor(DSL.rejectReport(id))
        def reportStream = stream