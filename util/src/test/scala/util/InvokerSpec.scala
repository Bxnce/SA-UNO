package util

import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl.UnoState
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class InvokerSpec extends AnyWordSpec {
  //"A Invoker " should {
  //  val i = new Invoker
  //  val game = new Game("p1", "p2", UnoState.between21State)
//
  //  "be able to execute given commands" in {
  //    game = i.doStep(TestCommand(game))
  //    game.pList(0).karten.size shouldBe (1)
  //    game = i.doStep(TestCommand(game))
  //    game.pList(0).karten.size shouldBe (2)
  //  }
  //  "remember these given Commands and undo them in the right order" in {
  //    game = i.undoStep.getOrElse(game)
  //    game.pList(0).karten.size shouldBe (1)
  //    game = i.undoStep.getOrElse(game)
  //    game.pList(0).karten.size shouldBe (0)
  //    i.undoStep shouldBe (None)
  //  }
  //  "should be able to redo the undone steps" in {
  //    game = i.redoStep.getOrElse(game)
  //    game.pList(0).karten.size shouldBe (1)
  //    game = i.redoStep.getOrElse(game)
  //    game.pList(0).karten.size shouldBe (2)
  //    i.redoStep shouldBe (None)
  //  }
  //}
}
