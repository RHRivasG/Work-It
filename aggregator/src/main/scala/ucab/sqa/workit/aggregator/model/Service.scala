package ucab.sqa.workit.aggregator.model

import cats.syntax.all._
import java.util.UUID
import java.net.InetAddress
import java.net.URI

case class Service private(id: UUID, host: URI, load: Int) {
    override def toString = 
        host.getPath
}

object Service {
    def of(id: String, host: String, load: Int) = for {
        id <- Either.catchNonFatal(UUID.fromString(id)).left.map(InvalidUUIDError)
        hostR <- Either.cond(host.trim != "", host, InvalidHostNameError(host))
        uri <- Either.catchNonFatal(new URI("grpc://" + hostR)).left.map(InvalidHostError)
    } yield Service(id, uri, load)
}