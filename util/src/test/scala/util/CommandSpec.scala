package util

import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.UnoState._
import model.gameComponent.gameInterface
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class TestCommand(game: gameInterface) extends Command[gameInterface] {
  var newgame: gameInterface = game
  override def execute: gameInterface =
    newgame = game.take("p1")
    newgame
  override def undoStep: gameInterface =
    game

  override def redoStep: gameInterface =
    newgame
}

class CommandSpec extends AnyWordSpec {
  val game = new Game("p1", "p2", between21State)
  val cmd = new TestCommand(game)
  "A Command " should {
    "have the method execute that changes the game in some way " in {
      val newgame  = cmd.execute
      newgame.pList.head.karten.size shouldBe 1
    }
    "have a method undo, that returns the state of the game before it was executed " in {
      val newgame = cmd.undoStep
      newgame.pList.head.karten.size shouldBe 0
    }
    "have a method redo, that redos it's execution after undoing it " in {
      val newgame = cmd.redoStep
      newgame.pList.head.karten.size shouldBe 1
    }
  }
}
