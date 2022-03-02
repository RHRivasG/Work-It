package ucab.sqa.workit.reports.infrastructure.db.lookup.sql

import cats.*
import cats.implicits.*
import cats.effect.implicits.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import java.util.UUID
import doobie.util.Get
import scala.util.Try
import cats.effect.kernel.Async
import doobie.util.fragment.Fragment
import ucab.sqa.workit.reports.infrastructure.db.lookup.LookupAction
import ucab.sqa.workit.reports.application.models.ReportModel
import doobie.util.Put
import ucab.sqa.workit.reports.application.queries.ReportQuery

final class SqlLookup[F[_]: Async](xa: Transactor[F]) extends (LookupAction ~> F):
    opaque type DbModel = (UUID, UUID, String, UUID)
    object DbModel:
        def apply(model: (UUID, UUID, String, UUID)): DbModel = model

        extension (model: DbModel)
            def toReportModel =
                ReportModel(model._1, model._2, model._4, model._3)

    import DbModel.*
    
    opaque type Query = Fragment
    object Query:
        given Get[UUID] = Get[String].temap(s => Try { UUID.fromString(s) }.toEither.leftMap(_.getMessage))
        extension (queryFragment: Fragment)
            def toReportModelOption(xa: Transactor[F]) =
                queryFragment
                .query[DbModel]
                .option
                .nested
                .map(_.toReportModel)
                .value
                .transact(xa)
            def toReportModelCollection(xa: Transactor[F]) =
                queryFragment
                .query[DbModel]
                .to[Vector]
                .nested
                .map(_.toReportModel)
                .value
                .transact(xa)
    
    import Query.*

    def apply[A](action: LookupAction[A]) = action match
        case LookupAction.GetAllReports => 
            sql"""SELECT * FROM "reports""""
            .toReportModelCollection(xa)
            .attempt
     
        case LookupAction.GetReport(id) =>
            sql"""SELECT * FROM "reports" WHERE "reports"."id" = ${id.toString}::uuid"""
            .toReportModelOption(xa)
            .attempt
        case LookupAction.GetReportsByTraining(id) =>
            sql"""SELECT * FROM "reports" WHERE "reports"."trainingId" = $id::uuid"""
            .toReportModelCollection(xa)
            .attempt
        case LookupAction.GetReportIssuedByUserOnTraining(id, trainingId) =>
            sql"""SELECT * FROM "reports" WHERE "reports"."trainingId" = $trainingId::uuid AND "reports"."issuerId" = $id::uuid"""
            .toReportModelOption(xa)
            .attempt