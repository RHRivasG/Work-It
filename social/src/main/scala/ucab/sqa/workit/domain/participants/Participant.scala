package ucab.sqa.workit.domain.participants

import ucab.sqa.workit.domain.participants.valueobjects.ParticipantName
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPassword
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantPreferences
import ucab.sqa.workit.domain.participants.valueobjects.ParticipantId
import ucab.sqa.workit.domain.participants.entities.ToTrainerRequest

case class Participant private (
    name: ParticipantName,
    password: ParticipantPassword,
    preferences: ParticipantPreferences,
    id: ParticipantId = ParticipantId.random,
    request: Option[ToTrainerRequest] = None
) {
  def update(name: ParticipantName, preferences: ParticipantPreferences) = {
    val currentPreferencesSet = Set(this.preferences.preferences: _*)
    val newPreferencesSet = Set(preferences.preferences: _*)
    val deletedPreferences = (currentPreferencesSet -- newPreferencesSet).toList
    val addedPreferences = (newPreferencesSet -- currentPreferencesSet).toList
    val removedPreferencesEvent = ParticipantPreferencesRemoved(
      this.id,
      new ParticipantPreferences(deletedPreferences)
    )
    val addedPreferencesEvent = ParticipantPreferencesAdded(
      this.id,
      new ParticipantPreferences(addedPreferences)
    )
    val updatedEvent = ParticipantUpdatedEvent(this.id, name)
    val eventList = List(updatedEvent) ++ (
      if (!(deletedPreferences.isEmpty)) List(removedPreferencesEvent)
      else List()
    ) ++ (
      if (!(addedPreferences.isEmpty)) List(addedPreferencesEvent)
      else List()
    )
    (
      eventList,
      this.copy(name = name, preferences = preferences)
    )
  }

  def requestToBecomeTrainer() = if (this.request.isDefined)
    Left(new Error("Request was already issued"))
  else
    for {
      (event, request) <- ToTrainerRequest.of(id)
    } yield (event, this.copy(request = Some(request)))

  def acceptRequestToBecomeTrainer() = for {
    request <- request.toRight(new Error("Request was not issued"))
  } yield request.accept(id, name, password, preferences)

  def rejectRequestToBecomeTrainer() = for {
    request <- request.toRight(new Error("Request was not issued"))
  } yield request.reject()

  def changePassword(password: ParticipantPassword) =
    if (password.password == this.password)
      Left(new Error("Passwords must be different"))
    else
      Right(
        (
          ParticipantPasswordChangedEvent(this.id, password),
          this.copy(password = password)
        )
      )

  def destroy =
    ParticipantDeletedEvent(this.id)
}

object Participant {
  def apply(
      name: ParticipantName,
      password: ParticipantPassword,
      preferences: ParticipantPreferences,
      id: ParticipantId = ParticipantId.random,
      request: Option[ToTrainerRequest] = None
  ) =
    (
      ParticipantCreatedEvent(id, name, password, preferences),
      new Participant(name, password, preferences, id, request)
    )

  def of(
      name: String,
      password: String,
      preferences: List[String],
      id: String = ""
  ): Either[Error, (ParticipantCreatedEvent, Participant)] =
    for (
      name <- ParticipantName.of(name);
      password <- ParticipantPassword.of(password);
      preferences <- ParticipantPreferences.of(preferences);
      id <- if (id == "") Right(ParticipantId.random) else ParticipantId.of(id)
    ) yield Participant(name, password, preferences, id)

  def unsafeOf(
      name: String,
      password: String,
      preferences: List[String],
      request: Option[String] = None,
      id: String = ""
  ) = new Participant(
    ParticipantName.unsafeOf(name),
    ParticipantPassword.unsafeOf(password),
    ParticipantPreferences.unsafeOf(preferences),
    ParticipantId.unsafeOf(id),
    request map { ToTrainerRequest.unsafeOf(_) }
  )
}
