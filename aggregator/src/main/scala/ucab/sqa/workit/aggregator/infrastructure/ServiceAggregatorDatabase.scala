package ucab.sqa.workit.aggregator.infrastructure

import io.circe._
import io.circe.syntax._
import cats._
import cats.effect._
import cats.implicits._
import cats.syntax._
import cats.instances.list._
import mongo4cats.circe._
import mongo4cats.client._
import java.util.UUID
import mongo4cats.client.MongoClient
import com.mongodb.reactivestreams.client.MongoDatabase
import mongo4cats.collection.MongoCollection
import com.mongodb.client.model.Filters
import mongo4cats.bson.ObjectId
import mongo4cats.collection.operations.Filter
import mongo4cats.bson
import java.net.URI
import ucab.sqa.workit.aggregator.model
import ucab.sqa.workit.aggregator.model.ServiceTable
import ucab.sqa.workit.aggregator.model.Operations._
import mongo4cats.collection.ReplaceOptions
import com.typesafe.config.ConfigFactory
import scala.util.Try

object ServiceAggregatorDatabase {
    private case class ServiceInfo(id: UUID, host: String, factor: Int)
    private case class ServiceEntry(group: String, services: List[ServiceInfo])

    private implicit val infoDecoder: Decoder[ServiceInfo] = Decoder.forProduct3("id", "host", "factor")(ServiceInfo.apply)
    private implicit val infoEncoder: Encoder[ServiceInfo] = Encoder.forProduct3("id", "host", "factor")(e => (e.id, e.host, e.factor))
    private implicit val entryDecoder: Decoder[ServiceEntry] = Decoder.forProduct2("group", "services")(ServiceEntry.apply)
    private implicit val entryEncoder: Encoder[ServiceEntry] = Encoder.forProduct2("group", "services")(e => (e.group, e.services))

    private val entriesCollection = for {
        config <- Resource.eval { IO.blocking { ConfigFactory.load("application.conf") } }
        host <- Resource.eval { IO.fromTry { Try { config.getString("db.host") } } }
        port <- Resource.eval { IO.fromTry { Try { config.getInt("db.port") } } }
        client <- MongoClient.fromServerAddress[IO](ServerAddress(host, port))
        db <- Resource.eval { client.getDatabase("serviceAggregator") }
        entries <- Resource.eval { db.getCollectionWithCodec[ServiceEntry]("entries") }
    } yield entries

    private def getTable(entries: MongoCollection[IO, ServiceEntry]) = for {
        list <- entries.find.all
        table = ServiceTable(
            list
            .groupMapReduce(d => model.Group(d.group))(_.services)(_ |+| _)
            .map { case (k, infos) => (k, infos.map{ info => 
                    (info.id, model.ServiceDescriptor(model.Service(info.id, URI.create(info.host), info.factor))) 
            }.toMap) }
        )
        ref <- Ref.of[IO, ServiceTable](table)
    } yield ref 

    private def storeTable(entries: MongoCollection[IO, ServiceEntry])(ref: Ref[IO, ServiceTable]) = IO.uncancelable { _ => 
        for {
            table <- ref.get
            model = table.table.map { case (group, t) => 
                        ServiceEntry(
                            group.name, 
                            t.values.map { d => ServiceInfo(d.service.id, d.service.host.toString, d.service.load )}.toList
                        ) 
                    }
                    .toList
            _ <- IO.println(f"Persisting ${model.length} groups with ${model.flatMap(_.services).length} services")
            _ <- entries.drop
            r <- entries.insertMany(model)
            _ <- IO.println(f"Persisted ${r.getInsertedIds().values().toArray().length} groups")
        } yield ()
    }

    def apply(): Resource[IO,Ref[IO, ServiceTable]] = for {
        collection <- entriesCollection
        table <- Resource.make(getTable(collection))(storeTable(collection))
    } yield table
}