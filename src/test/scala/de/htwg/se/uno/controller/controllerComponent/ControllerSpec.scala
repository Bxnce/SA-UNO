package de.htwg.se.uno
package controller.controllerComponent.controllerBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.Card._
import model.gameComponent.gameBaseImpl.CardLayout.eol
import controller._

class ControllerSpec extends AnyWordSpec {
  "Controller" should {
    var game = new Game("p1", "p2", between21State)
    game = game.addTest("midstack", R0)
    game = game.add("p1", B0)
    var c = new Controller(game)

    "contain a game" in {
      c.game.pList(0).karten.size shouldBe (1)
      c.game.pList(1).karten.size shouldBe (0)
      c.game.midCard.karten.size shouldBe (1)
      c.game.midCard.karten(0) shouldBe (R0)

    }
    "have a method take(), that calls the take() function in game" in {
      c.game.currentstate shouldBe (between21State)
      c.next() //p1s
      c.take()
      c.game.ERROR shouldBe (0)
      c.next() //p2n
      c.take()
      c.game.ERROR shouldBe (-1)
      c.next() //p2s
      c.take()
      c.game.ERROR shouldBe (0)
    }

    "have a method place() that places a Card from a player to the midStack" in {
      c.next() //p1n
      c.next() //p1s
      c.place(0)
      c.game.ERROR shouldBe (0)
      c.game.midCard.karten(0) shouldBe (B0)
      c.game.currentstate shouldBe (between12State)
      c.place(0)
      c.game.ERROR shouldBe (-1)
    }
    "have a method next()" in {
      c.game.currentstate shouldEqual (between12State)
      c.next() //p2s
      c.game.currentstate shouldEqual (player2State)
    }

    var game1 = new Game("p1", "p2", between21State)
    game1 = game1.addTest("midstack", R0)
    game1 = game1.add("p1", R1)
    var c1 = new Controller(game1)

    "have a method undo() that undos the last Command" in {
      c1.undo()
      c1.game shouldEqual (game1)

      c1.next()
      c1.game.currentstate shouldEqual (player1State)
      c1.undo()
      c1.game.currentstate shouldEqual (between21State)

      c1.next() //p1s

      c1.game.pList(0).karten.size shouldBe (1)
      c1.take()
      c1.game.pList(0).karten.size shouldBe (2)
      c1.undo()
      c1.game.pList(0).karten.size shouldBe (1)

      c1.take() //dass er nicht gewinnt
      c1.place(0) //p2n
      c1.game.pList(0).karten.size shouldBe (1)
      c1.game.midCard.karten(0) shouldBe (R1)
      c1.undo()
      c1.game.pList(0).karten.size shouldBe (2)
      c1.game.midCard.karten(0) shouldBe (R0)
    }

    var game2 = new Game("p1", "p2", between21State)
    game2 = game2.addTest("midstack", R0)
    game2 = game2.add("p1", R1)
    var c2 = new Controller(game2)

    "have a method redo() that redos the last step that happend" in {
      c2.next()
      c2.game.currentstate shouldEqual (player1State)
      c2.undo()
      c2.game.currentstate shouldEqual (between21State)
      c2.redo()
      c2.game.currentstate shouldEqual (player1State)

      c2.take()
      c2.game.pList(0).karten.size shouldBe (2)
      c2.undo()
      c2.game.pList(0).karten.size shouldBe (1)
      c2.redo()
      c2.game.pList(0).karten.size shouldBe (2)

      c2.place(0)
      c2.game.pList(0).karten.size shouldBe (1)
      c2.game.midCard.karten(0) shouldBe (R1)
      c2.undo()
      c2.game.pList(0).karten.size shouldBe (2)
      c2.game.midCard.karten(0) shouldBe (R0)
      c2.redo()
      c2.game.pList(0).karten.size shouldBe (1)
      c2.game.midCard.karten(0) shouldBe (R1)
    }

    "have a method newG(String,String) that creates a new Game" in {
      var game2 = new Game("p1", "p2", between21State)
      c = new Controller(game2)

      c.newG("Bence", "Timo")
      c.game.pList(0).name shouldBe ("Bence")
      c.game.pList(1).name shouldBe ("Timo")
      c.game.pList(0).karten.size shouldBe (7)
      c.game.pList(1).karten.size shouldBe (7)
      c.game.midCard.karten.size shouldBe (1)
      c.game.currentstate shouldEqual (between21State)
    }

    "have a method colorChoose(String), that is called after a wildcard or a take4" in {
      var game = new Game("p1", "p2", between21State)
      var c = new Controller(game)

      c.colorChoose("Red")
      c.game.midCard.karten(0) shouldBe (R)

      c.colorChoose("Green")
      c.game.midCard.karten(0) shouldBe (G)

      c.colorChoose("Blue")
      c.game.midCard.karten(0) shouldBe (B)

      c.colorChoose("Yellow")
      c.game.midCard.karten(0) shouldBe (Y)

      c.colorChoose("")
      c.game.midCard.karten(0) shouldBe (Y)

      c.colorChoose("Hallo")
      c.game.midCard.karten(0) shouldBe (Y)
    }

    "override the method toString" in {
      var game2 = new Game("p1", "p2", between21State)
      game2 = game2.addTest("midstack", R0)
      game2 = game2.add("p1", R1)
      game2 = game2.add("p2", G1)
      var c2 = new Controller(game2)

      c2.toString shouldBe (UnoCommand(c2, "print").toString)
      c2.next()
      c2.toString shouldBe (UnoCommand(c2, "print").toString)
    }

  }
}
