package fileIOComponent

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import fileIOComponent.database.{DAOInterface, MongoDAO, SlickDAO}
import net.codingwell.scalaguice.ScalaModule


class PersistenceModule extends AbstractModule:
  override def configure(): Unit =
  //bind(classOf[DAOInterface]).annotatedWith(Names.named("slick")).to(classOf[SlickDAO])
    bind(classOf[DAOInterface]).annotatedWith(Names.named("mongodb")).to(classOf[MongoDAO])

