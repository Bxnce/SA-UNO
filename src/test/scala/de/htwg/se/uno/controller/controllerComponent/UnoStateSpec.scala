package de.htwg.se.uno
package controller.controllerComponent.controllerBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.Card._
import model.gameComponent.gameBaseImpl.CardLayout.eol

class UnoStateSpec extends AnyWordSpec {
  "UnoState" should {
    var game = new Game("p1", "p2", between21State)
    game = game.addTest("midstack", R0)
    game = game.add("p1", B0)
    game = game.add("p1", R7)
    game = game.add("p2", G0)
    var c = new Controller(game)
    val takeC = UnoCommand(c, "take")
    val placeC = UnoCommand(0, c)
    val nextC = UnoCommand(c, "next")

    "player1State should have a method handle() that matches the commands" in {
      c.game = player1State.handle(takeC)
      c.game.pList(0).karten.size shouldBe (3)

      c.game = player1State.handle(placeC)
      c.game.ERROR shouldBe (0)
      c.game = player1State.handle(placeC)
      c.game.ERROR shouldBe (-1)

      c.game = player1State.handle(nextC)
      c.game.currentstate shouldBe (between12State)
    }

    "player2State should have a method handle() that matches the commands" in {
      c.game = player2State.handle(nextC)
      c.game.currentstate shouldBe (between21State)

      c.game = player2State.handle(placeC)
      c.game.currentstate shouldBe (winState)
    }

    "between12State should have a method handle() that matches the commands" in {
      c.game = between12State.handle(takeC)
      c.game.ERROR shouldBe (-1)

      c.game = between12State.handle(placeC)
      c.game.ERROR shouldBe (-1)

      c.game = between12State.handle(nextC)
      c.game.ERROR shouldBe (0)
    }
    "between21State should have a method handle() that matches the commands" in {
      c.game = between21State.handle(takeC)
      c.game.ERROR shouldBe (-1)

      c.game = between21State.handle(placeC)
      c.game.ERROR shouldBe (-1)

      c.game = between21State.handle(nextC)
      c.game.ERROR shouldBe (0)
    }

    "winState should have a method handle() that matches the commands" in {
      c.game = winState.handle(takeC)
      c.game.ERROR shouldBe (-1)

      c.game = winState.handle(placeC)
      c.game.ERROR shouldBe (-1)

      c.game = winState.handle(nextC)
      c.game.ERROR shouldBe (0)
    }

    //let player 1 win as well
    var game1 = new Game("p1", "p2", between12State)
    game1 = game1.addTest("midstack", R0)
    game1 = game1.add("p1", B0)
    game1 = game1.add("p2", G0)
    game1 = game1.add("p2", R7)
    var c1 = new Controller(game1)
    val takeC1 = UnoCommand(c1, "take")
    val placeC1 = UnoCommand(0, c1)
    val nextC1 = UnoCommand(c1, "next")

    "make sure that both players can win" in {
      c1.game = player2State.handle(takeC1)
      c1.game.pList(1).karten.size shouldBe (3)

      c1.game = player2State.handle(placeC1)
      c1.game = player2State.handle(placeC1)
      c1.game.ERROR shouldBe (-1)

      c1.game = player1State.handle(placeC1)
      c1.game.currentstate shouldBe (winState)
    }

  }
}
