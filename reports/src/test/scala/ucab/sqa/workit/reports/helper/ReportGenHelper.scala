package ucab.sqa.workit.reports.helper

import ucab.sqa.workit.reports.domain.values.*
import ucab.sqa.workit.reports.domain.Report
import org.scalacheck.Arbitrary.*
import org.scalacheck.Gen
import cats.syntax.all.*
import java.util.UUID
import scala.util.Try

trait ReportGenHelper {
    private def validReason(reason: String) = reason.length > 0 && reason.length <= 255
    val validReasonGen = arbitrary[String] suchThat validReason
    val veryLargeReasonGen = arbitrary[String] suchThat { _.length > 255 }
    val emptyReasonGen = Gen.stringOfN(0, Gen.alphaChar)

    private def invalidId(id: String) = Try(UUID.fromString(id)).fold(_ => true, _ => false)
    val validIdGen = arbitrary[UUID] map { _.toString }
    val invalidIdGen = arbitrary[String] suchThat invalidId

    def reports(
        reasonGen: Gen[String] = validReasonGen, 
        idGen: Gen[String] = validIdGen,
        trainingIdGen: Gen[String] = validIdGen,
        issuerIdGen: Gen[String] = validIdGen
    ) = for 
        id <- idGen
        trainingId <- trainingIdGen
        issuerId <- issuerIdGen
        reason <- reasonGen
    yield Report.of(id, issuerId, trainingId, reason.toString).asEither.map(_._2)
}