package ucab.sqa.workit.reports.infrastructure

import cats.*
import cats.implicits.*
import cats.syntax.all.*
import cats.effect.IO
import ucab.sqa.workit.reports.application.*
import ucab.sqa.workit.reports.domain.values.*
import ucab.sqa.workit.reports.infrastructure.InfrastructureError

package object shared:
    type InfrastructureResult[A] = IO[A]
    type InfrastructureExecutor = ReportAction ~> InfrastructureResult