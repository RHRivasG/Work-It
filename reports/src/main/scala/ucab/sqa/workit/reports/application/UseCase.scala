package ucab.sqa.workit.reports.application

import cats.~>
import cats.syntax.all.*
import ucab.sqa.workit.reports.application.dsl.ApplicationDSL as DSL
import ucab.sqa.workit.reports.application.models.ReportModel
import cats.Monad
import cats.MonadError
import ucab.sqa.workit.reports.domain.errors.DomainError
import cats.data.NonEmptyList

type ErrorHandler = NonEmptyList[DomainError] => Throwable
type Compiler[F[_]] = ReportInput ~> F
type Interpreter[G[_], F[_]] = G ~> F

sealed trait UseCase[F[_]]:
    def report(id: String): F[ReportModel]
    def reports: F[Vector[ReportModel]]
    def reportsOfTraining(id: String): F[Vector[ReportModel]]
    def issueReport(issuer: String, training: String, reason: String): F[Unit]
    def acceptReport(id: String): F[Unit]
    def rejectReport(id: String): F[Unit]

object UseCase:
    def apply[F[_]](using uc: UseCase[F]) = uc
    def build[G[_], F[_]: [F[_]] =>> MonadError[F, Throwable]](
        errorHandler: ErrorHandler,
        compiler: Compiler[G],
        interpreter: Interpreter[G, F]
    ) = new UseCase[F]:
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