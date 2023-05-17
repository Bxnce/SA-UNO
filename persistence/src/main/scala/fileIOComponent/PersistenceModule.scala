package fileIOComponent

import com.google.inject.AbstractModule
import fileIOComponent.database.mongodb.{MongoDAO, SimpleMongoDAO}
import fileIOComponent.database.{DAOInterface, SlickDAO}
import net.codingwell.scalaguice.ScalaModule


class PersistenceModule extends AbstractModule:
  override def configure(): Unit =
  //bind(classOf[DAOInterface]).to(classOf[SlickDAO])
  //bind(classOf[DAOInterface]).to(classOf[MongoDAO])
    bind(classOf[DAOInterface]).to(classOf[SimpleMongoDAO])

