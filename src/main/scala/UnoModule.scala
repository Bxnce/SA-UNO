package scala

import com.google.inject.{AbstractModule, Guice, Inject}
import controller.controllerComponent.controllerInterface
import model.gameComponent.gameInterface
import net.codingwell.scalaguice.ScalaModule

class UnoModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[controllerInterface]).toInstance(
      Controller(new Game("place_h", "place_h", between21State))
    )
    bind(classOf[FileIOInterface]).toInstance(JSONImpl.fileIO())
  }

}
