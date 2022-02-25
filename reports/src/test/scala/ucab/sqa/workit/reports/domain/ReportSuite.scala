package ucab.sqa.workit.reports.domain

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.Gen.*
import org.scalacheck.Arbitrary.*
import cats.syntax.all.*
import cats.instances.UUIDInstances
import java.util.UUID
import ucab.sqa.workit.reports.domain.values.*
import ucab.sqa.workit.reports.domain.errors.DomainError
import ucab.sqa.workit.reports.helper.ReportGenHelper
import cats.data.NonEmptyList

class ReportSuite extends ScalaCheckSuite with ReportGenHelper {
    property("Reports are valid when all ids and reason are valid") {
        forAll(reports()) { result =>
            ("Result is valid"                              |: result.isRight) &&
            ("ID is not null"                               |: result.map(_.id.value) != Right(null)) &&
            ("ID is a valid UUID"                           |: result.map(r => UUID.fromString(r.id.value.toString)) == result.map(_.id.value)) &&
            ("Training ID is a valid UUID"                  |: result.map(r => UUID.fromString(r.training.value.toString)) == result.map(_.training.value)) &&
            ("Issuer ID is a valid UUID"                    |: result.map(r => UUID.fromString(r.issuer.value.toString)) == result.map(_.issuer.value)) &&
            ("Reason is not empty"                          |: result.map(_.reason.value).fold(_ => false, !_.isEmpty)) &&
            ("Reason has less than 256 characters"          |: result.map(_.reason.value).fold(_ => false, _.length <= 255))
        }
    }

    property("Reports are invalid when an empty reason is provided") {
        forAll(reports(reasonGen = emptyReasonGen)) { result =>
            ("Result is invalid"    |: result.isLeft) &&
            ("Reason is empty"      |: (result match 
                case Right(_) => false
                case Left(nel) => nel.exists(_.isInstanceOf[DomainError.ReasonEmptyError])))
        }
    }

    property("Reports are invalid when a very large reason is provided") {
        forAll(reports(reasonGen = veryLargeReasonGen)) { result =>
            ("Result is invalid"            |: result.isLeft) &&
            ("Reason max length surpased"   |: (result match 
                case Right(_) => false
                case Left(nel) => nel.exists(_.isInstanceOf[DomainError.ReasonMaxLengthSurpasedError])))
        }
    }

    property("Reports are invalid when an invalid UUID is provided as ID") {
        forAll(reports(idGen = invalidIdGen)) { result =>
            ("Result is invalid"                    |: result.isLeft) &&
            ("UUID given as ID is invalid"          |: (result match 
                case Right(_) => false
                case Left(nel) => nel.exists(_.isInstanceOf[DomainError.InvalidUUIDError])))
        }
    }

    property("Reports are invalid when an invalid UUID is provided as Training ID") {
        forAll(reports(trainingIdGen = invalidIdGen)) { result =>
            ("Result is invalid"                        |: result.isLeft) &&
            ("UUID given as Training ID is invalid"     |: (result match 
                case Right(_) => false
                case Left(nel) => nel.exists(_.isInstanceOf[DomainError.InvalidUUIDError])))
        }
    }

    property("Reports are invalid when an invalid UUID is provided as Issuer ID") {
        forAll(reports(issuerIdGen = invalidIdGen)) { result =>
            ("Result is invalid"                    |: result.isLeft) &&
            ("UUID given as Issuer ID is invalid"   |: (result match 
                case Right(_) => false
                case Left(nel) => nel.exists(_.isInstanceOf[DomainError.InvalidUUIDError])))
        }
    }
}