package scala


import com.google.inject.{AbstractModule, Guice, Inject}
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.controllerInterface
import controller.controllerComponent.controllerBaseImpl.Controller
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl.Game
import model.fileIOComponent._
import model.fileIOComponent.FileIOInterface
import model.fileIOComponent.JSONImpl.fileIO
import model.gameComponent.gameBaseImpl.UnoState

class UnoModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[controllerInterface]).toInstance(
      Controller(new Game("place_h", "place_h", UnoState.between21State))
    )
    bind(classOf[FileIOInterface]).toInstance(JSONImpl.fileIO())
  }

}