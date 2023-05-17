package fileIOComponent.database

import org.mongodb.scala.model.{Aggregates, Sorts}
import org.mongodb.scala.{Document, MongoCollection, SingleObservable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf


protected def getHighestId(coll: MongoCollection[Document]): Int =
  val result = Await.result(coll.aggregate(Seq(
    Aggregates.sort(Sorts.descending("_id")),
    Aggregates.limit(1),
    Aggregates.project(Document("_id" -> 1))
  )).headOption(), Inf)
  result.flatMap(_.get("_id").map(_.asInt32().getValue.toHexString)).getOrElse("0").toInt

protected def handleResult[T](obs: SingleObservable[T]): Unit =
  Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), WAIT_TIME)
  println("db operation successful")

