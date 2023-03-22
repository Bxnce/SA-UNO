package de.htwg.se.uno
package aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.Game._
import model.gameComponent.gameBaseImpl.Card._
import model.gameComponent.gameBaseImpl.CardLayout._
import controller.controllerComponent.controllerBaseImpl.Controller
import util._
import controller.controllerComponent.controllerBaseImpl._

class TUISpec extends AnyWordSpec {
  "TUI" when {
    var game = new Game("Bence", "Timo", between21State)
    game = game.addTest("midStack", Y0)
    game = game.add("P1", R0)
    game = game.add("P1", R1)
    game = game.add("P1", R2)
    game = game.add("P2", G0)
    game = game.add("P2", G1)
    game = game.add("P2", G2)
    val controller = Controller(game)
    val tui = TUI(controller)

    "created with the given parameters " should {
      "have the following values" in {
        controller.toString shouldEqual (
          "Bence\n" +
            "+--+\n" +
            "| 3|\n" +
            "+--+\n" +
            "\n" +
            "+--+\n" +
            "|Y0|\n" +
            "+--+\n" +
            "\n" +
            "+--+\n" +
            "| 3|\n" +
            "+--+\n" +
            "Timo\n"
        )
      }
      "have a method run(String) that checks the output of convertInputString" in {
        tui.run("") shouldBe (tui.printhelp())
      }
      "have a method convertinputString(String) that calls the controller to execute the command" in {
        tui.convertinputString("") shouldBe (tui.ERROR)
        tui.convertinputString("a") shouldBe (tui.ERROR)
        tui.convertinputString("help") shouldBe (tui.SUCCESS)

        tui.convertinputString("n") shouldBe (tui.SUCCESS) //player1State

        tui.convertinputString("+") shouldBe (tui.SUCCESS)
        controller.game.pList(0).karten.size shouldBe (4)

        tui.convertinputString("-") shouldBe (tui.ERROR)
        tui.convertinputString("- 1") shouldBe (tui.SUCCESS) //p2n
        controller.game.pList(0).karten.size shouldBe (3)
        controller.game.midCard.karten(0) shouldBe (R0)

        tui.convertinputString("+") shouldBe (tui.ERROR)
        tui.convertinputString("- 1") shouldBe (tui.ERROR)

        tui.convertinputString("n") shouldBe (tui.SUCCESS) //p2s
        controller.game.currentstate shouldBe (player2State)

        tui.convertinputString("+")
        controller.game.pList(1).karten.size shouldBe (4)
        tui.convertinputString("z") shouldBe (tui.SUCCESS)
        controller.game.pList(1).karten.size shouldBe (3)
        tui.convertinputString("y") shouldBe (tui.SUCCESS)
        controller.game.pList(1).karten.size shouldBe (4)

      }
      "Have a method printhelp() that prints out the help message" in {}
      "override the method update" in {}
    }
  }
}
