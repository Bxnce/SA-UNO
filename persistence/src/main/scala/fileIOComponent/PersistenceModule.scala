package fileIOComponent

import com.google.inject.AbstractModule
import fileIOComponent.database.{DAOInterface, MongoDAO, SlickDAO}
import net.codingwell.scalaguice.ScalaModule


class PersistenceModule extends AbstractModule:
  override def configure(): Unit =
  //bind(classOf[DAOInterface]).to(classOf[SlickDAO])
    bind(classOf[DAOInterface]).to(classOf[MongoDAO])

