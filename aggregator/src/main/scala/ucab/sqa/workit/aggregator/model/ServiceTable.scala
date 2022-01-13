package ucab.sqa.workit.aggregator.model

import java.util.UUID
import cats.syntax.all._
import java.net.URI

case class ServiceTable(table: Map[Group, Map[UUID, ServiceDescriptor]]) {
    private def newTableFrom(service: Service) =
        Map(service.id -> ServiceDescriptor(service))

    def addService(group: Group, service: Service) =
        copy(table = table.updatedWith(group) { _.fold(newTableFrom(service))(_.updated(service.id, ServiceDescriptor(service))).some } )
    
    def removeService(group: Group, host: String) = for {
        serviceTable <- table.get(group).toRight(GroupNotFound(group))
        (id, _) <- serviceTable.find {
            case (_, d) => d.service.host == new URI(host)
        }.toRight(ServiceWithHostNotFound(host))
    } yield copy(table = table.updatedWith(group) { _.map(_.removed(id)) })
    
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