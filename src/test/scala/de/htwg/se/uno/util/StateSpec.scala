package de.htwg.se.uno
package util

import model.gameComponent.gameBaseImpl.Game
import controller.controllerComponent.controllerBaseImpl.between21State
import controller.controllerComponent.controllerBaseImpl.Controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.uno.model.gameComponent.gameInterface

class StateSpec extends AnyWordSpec {
  val game = new Game("p1", "p2", between21State)
  val c = new Controller(game)
  val cmd = new TestCommand(c)
  var counter = 0

  object TestState extends State {
    override def handle(command: Command): gameInterface =
      command match
        case e: TestCommand =>
          counter = counter + 5
          new Game("p1", "p2", between21State)
  }

  "State " should {
    "have a method handle(Command) that looks wich kind of Command it has recieved and then does something" in {
      TestState.handle(cmd) shouldBe (game)
      counter shouldBe (5)
      TestState.handle(cmd) shouldBe (game)
      counter shouldBe (10)
    }
  }

}
