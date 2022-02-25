package ucab.sqa.workit.reports.infrastructure.http.error

import cats.syntax.all.*

case class ApiError(`type`: String, title: String, status: Option[Int], instance: Option[String], problems: List[ApiError])

object ApiError:
    def singleton(`type`: String, title: String, status: Int, instance: String) = 
        ApiError(`type`, title, status.some, instance.some, List())
    def sub(`type`: String, title: String) = ApiError(`type`, title, None, None, List())
