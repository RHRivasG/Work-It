package ucab.sqa.workit.aggregator.model

import cats.kernel.Monoid
import scala.collection.immutable.HashMap

object Operations {
    implicit object ServiceTableMonoid extends Monoid[ServiceTable] {

      override def combine(x: ServiceTable, y: ServiceTable): ServiceTable = 
          ServiceTable(x.table ++ y.table)

      override def empty: ServiceTable = ServiceTable(HashMap.empty)

    }
}