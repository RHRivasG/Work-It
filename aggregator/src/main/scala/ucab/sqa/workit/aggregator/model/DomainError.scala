package ucab.sqa.workit.aggregator.model

import java.util.UUID

sealed trait DomainError

final case class InvalidUUIDError(e: Throwable) extends DomainError
final case class InvalidHostNameError(name: String) extends DomainError
final case class InvalidHostError(e: Throwable) extends DomainError
final case class LoadBelowZero(id: UUID) extends DomainError
final case class GroupNotFound(group: Group) extends DomainError
final case class GroupNameEmpty() extends DomainError
final case class ServiceWithHostNotFound(host: String) extends DomainError
final case class ServiceNotFound(id: UUID) extends DomainError