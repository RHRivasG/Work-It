package ucab.sqa.workit.web

//#json-formats
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat
import spray.json.JsObject
import spray.json.JsArray
import spray.json.JsString
import spray.json.JsValue
import spray.json.JsonFormat
import _root_.ucab.sqa.workit.application.participants.CreateParticipantCommand
import _root_.ucab.sqa.workit.application.participants.UpdateParticipantCommand
import _root_.ucab.sqa.workit.domain.participants.valueobjects.Preference
import _root_.ucab.sqa.workit.application.trainers.UpdateTrainerCommand
import _root_.ucab.sqa.workit.application.participants.ParticipantModel
import _root_.ucab.sqa.workit.application.trainers.TrainerModel
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ucab.sqa.workit.web.profile.Profile

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val participantModelFormat =
    jsonFormat3(ParticipantModel)

  implicit val trainerModelFormat =
    jsonFormat3(TrainerModel)

  case class PartialUpdateParticipantCommand(
      name: String,
      preferences: List[String]
  )

  case class PartialChangePasswordParticipantCommand(
      password: String
  )

  implicit def profileFormat[A: JsonFormat] = jsonFormat2(Profile.apply[A])
  implicit val createParticipantFormat = jsonFormat3(CreateParticipantCommand)
  implicit val updateParticipantFormat = jsonFormat2(
    PartialUpdateParticipantCommand
  )
  implicit val changePasswordParticipantFormat = jsonFormat1(
    PartialChangePasswordParticipantCommand
  )
  implicit val preferenceFormat = jsonFormat1(Preference.unsafeOf(_))

  case class PartialUpdateTrainerCommand(
      name: String,
      preferences: List[String]
  )

  case class PartialChangePasswordTraninerCommand(
      password: String
  )

  implicit val updateTrainerFormat = jsonFormat2(
    PartialUpdateTrainerCommand
  )

  implicit val changePasswordTrainerFormat = jsonFormat1(
    PartialChangePasswordTraninerCommand
  )
}
//#json-formats
