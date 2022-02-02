package ucab.sqa.workit.reports.infrastructure.repository

package object mongodb {

}
// import java.util.UUID
// import cats.MonadError
// import cats.effect.kernel.Async
// import cats.effect.kernel.Resource
// import ucab.sqa.workit.reports.domain.Report
// import ucab.sqa.workit.reports.domain.errors.ReportNotFoundError
// import mongo4cats.client.MongoClient
// import mongo4cats.collection.operations.Filter
// import mongo4cats.circe._
// import io.circe.Encoder
// import io.circe.Decoder
// import ucab.sqa.workit.reports.infrastructure.errors.ReportError
// import ucab.sqa.workit.reports.infrastructure.errors.ReportDomainError
// import ucab.sqa.workit.reports.infrastructure.errors.ReportInfrastructureError
// import mongo4cats.collection.operations.Update

// package object mongodb {
//     private implicit val reportEncoder: Encoder[Report] = Encoder.forProduct2("id", "trainingId")(r => (r.id.toString, r.trainingId.toString))
//     private implicit val reportDecoder: Decoder[Report] = Decoder.forProduct2[Report, UUID, UUID]("id", "trainingId"){
//         (id, trainingId) => Report(id, trainingId)
//     }

//     def wrapError[F[_]: Async : MonadError[*[*], ReportError], A](f: => F[A]) =
//         Async[F].handleErrorWith(f) { err =>
//             MonadError[F, ReportError].raiseError(ReportInfrastructureError(err))
//         }

//     private def connection[F[_]: Async] = MongoClient.fromConnectionString[F]("mongodb://localhost")

//     implicit def mongoRepository[F[_]: Async](implicit F: MonadError[F, ReportError]) = for {
//         connection <- connection[F]
//         db <- Resource.eval { connection.getDatabase("work-it") }
//         collection <- Resource.eval { db.getCollectionWithCodec[Report]("reports") }
//     } yield new Repository[F] {

//       override def get(id: String): F[Report] = 
//         wrapError {
//           F.flatMap(collection.find(Filter.eq("id", id)).first) { result => result match {
//               case Some(value) => MonadError[F, ReportError].pure(value)
//               case None => MonadError[F, ReportError].raiseError(ReportDomainError(ReportNotFoundError(id)))
//           } }
//         }

//       override def getAll: F[Vector[Report]] = 
//         wrapError {
//           F.map(collection.find.all) { it => Vector.from(it) }
//         }


//       override def create(id: UUID, trainingId: UUID): F[Unit] = 
//         wrapError {
//           F.flatMap(collection.insertOne(Report(id, trainingId))) { rs =>
//              if (!rs.getInsertedId().isNull() && rs.wasAcknowledged()) F.pure(())
//              else F.raiseError(ReportInfrastructureError(new Error(s"Error inserting report with id: $id and trainingId: $trainingId")))
//           }
//         }

//       override def update(id: UUID, trainingId: UUID): F[Unit] = 
//         wrapError {
//           F.flatMap(collection.updateOne(Filter.eq("id", id), Update.set("trainingId", trainingId))) { rs =>
//             if (rs.getModifiedCount() >= 1 && rs.wasAcknowledged()) F.pure(())
//             else F.raiseError(ReportInfrastructureError(new Error(s"Error updating report with id: $id")))
//           }
//         }

//       override def delete(id: UUID): F[Unit] = 
//         wrapError {
//             F.flatMap(collection.deleteOne(Filter.eq("id", id))) { rs =>
//               if (rs.getDeletedCount() >= 1 && rs.wasAcknowledged()) F.pure(())
//               else F.raiseError(ReportInfrastructureError(new Error(s"Error deleting report with id $id")))
//             }
//         }
//     }
// }
