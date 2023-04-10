package util

import de.htwg.se.uno.controller.controllerComponent.controllerInterface
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.Game
import de.htwg.se.uno.model.gameComponent.gameInterface
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class TestCommand(controller: controllerInterface) extends Command(controller) {
  override def execute =
    newgame = controller.game.take("p1")
    newgame
}

class CommandSpec extends AnyWordSpec {
  val game = new Game("p1", "p2", between21State)
  val c = new Controller(game)
  val cmd = new TestCommand(c)
  "A Command " should {
    "have the method execute that changes the game in some way " in {
      c.game.pList(0).karten.size shouldBe (0)
      c.game = cmd.execute
      c.game.pList(0).karten.size shouldBe (1)
    }
    "have a method undo, that returns the state of the game before it was executed " in {
      c.game = cmd.undoStep
      c.game.pList(0).karten.size shouldBe (0)
    }
    "have a method redo, that redos it's execution after undoing it " in {
      c.game = cmd.redoStep
      c.game.pList(0).karten.size shouldBe (1)
    }
  }
}