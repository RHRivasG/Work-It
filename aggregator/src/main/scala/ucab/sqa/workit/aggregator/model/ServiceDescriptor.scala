package ucab.sqa.workit.aggregator.model

case class ServiceDescriptor private (service: Service, loadCount: Int) {
    def count =
        if (loadCount == 0) Left(LoadBelowZero(this.service.id))
        else Right(copy(loadCount = loadCount - 1))

    def reset =
        copy(loadCount = service.load)
    
    def loadFactor = 
        loadCount.toFloat / service.load.toFloat
}

object ServiceDescriptor {
    def apply(service: Service) = new ServiceDescriptor(service, service.load)
}