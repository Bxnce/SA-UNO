package controller

import com.google.inject.{AbstractModule, Guice, Inject}
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.controllerInterface
import controller.controllerComponent.controllerBaseImpl.Controller
import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.UnoState

object RestModule:
  given controllerInterface = Controller(new Game("place_h", "place_h", UnoState.between21State).init())

class RestModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[controllerInterface]).toInstance(
      Controller(new Game("place_h", "place_h", UnoState.between21State))
    )
  }

}