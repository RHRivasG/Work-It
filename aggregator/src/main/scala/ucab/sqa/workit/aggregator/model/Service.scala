package ucab.sqa.workit.aggregator.model

import cats.syntax.all.*
import java.util.UUID
import java.net.URI

case class Service(id: UUID, host: URI, load: Int) {
    override def toString = 
        host.getPath
}

object Service {
    def of(id: String, host: String, load: Int) = for {
        id <- Either.catchNonFatal(UUID.fromString(id)).left.map(InvalidUUIDError.apply)
        hostR <- Either.cond(host.trim != "", host, InvalidHostNameError(host))
        uri <- Either.catchNonFatal(new URI("grpc://" + hostR)).left.map(InvalidHostError.apply)
    } yield Service(id, uri, load)
}