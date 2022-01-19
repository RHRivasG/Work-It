package ucab.sqa.workit.aggregator.model

import java.util.UUID
import cats.syntax.all._
import java.net.URI

case class ServiceTable(table: Map[Group, Map[UUID, ServiceDescriptor]]) {
    private def newTableFrom(service: Service) =
        Map(service.id -> ServiceDescriptor(service))

    def addService(group: Group, service: Service) =
        copy(table = table.updatedWith(group) { _.fold(newTableFrom(service))(_.updated(service.id, ServiceDescriptor(service))).some } )
    
    def unsubscribeHost(host: URI) = {
        println(f"Removing $host")
        copy(table = table.map {
            case (g, map) =>
                val keys = map.filter(_._2.service.host.getHost == host.getHost).map(_._1)
                keys.foreach { uri => println(f"Found matching host $uri")}
                (g, keys.foldLeft(map)(_.removed(_)))
        })
    }
    
    def nextService(group: Group) = for {
        services <- table.get(group).toRight(GroupNotFound(group))
        descriptor = services.values.toList.sortBy(- _.loadFactor).head
        updatedDescriptor <- descriptor.count
        rotatedDescriptors = services.updated(descriptor.service.id, updatedDescriptor)
        updatedDescriptors = 
            if (rotatedDescriptors.forall(_._2.loadCount == 0)) 
                rotatedDescriptors.map { case (id, d) => (id, d.reset) }
            else
                rotatedDescriptors
        updatedTable = table.updated(group, updatedDescriptors)
    } yield updatedDescriptor.service -> copy(table = updatedTable)
}