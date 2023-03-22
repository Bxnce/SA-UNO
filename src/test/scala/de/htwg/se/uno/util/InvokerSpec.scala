package de.htwg.se.uno
package util

import model.gameComponent.gameBaseImpl.Game
import controller.controllerComponent.controllerBaseImpl.between21State
import controller.controllerComponent.controllerBaseImpl.Controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class InvokerSpec extends AnyWordSpec {
  "A Invoker " should {
    val i = new Invoker
    val game = new Game("p1", "p2", between21State)
    val c = new Controller(game)

    "be able to execute given commands" in {
      c.game = i.doStep(TestCommand(c))
      c.game.pList(0).karten.size shouldBe (1)
      c.game = i.doStep(TestCommand(c))
      c.game.pList(0).karten.size shouldBe (2)
    }
    "remember these given Commands and undo them in the right order" in {
      c.game = i.undoStep.getOrElse(c.game)
      c.game.pList(0).karten.size shouldBe (1)
      c.game = i.undoStep.getOrElse(c.game)
      c.game.pList(0).karten.size shouldBe (0)
      i.undoStep shouldBe (None)
    }
    "should be able to redo the undone steps" in {
      c.game = i.redoStep.getOrElse(c.game)
      c.game.pList(0).karten.size shouldBe (1)
      c.game = i.redoStep.getOrElse(c.game)
      c.game.pList(0).karten.size shouldBe (2)
      i.redoStep shouldBe (None)
    }
  }
}
