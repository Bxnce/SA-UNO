package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class CardsSpec extends AnyWordSpec {
  "Cards" should {
    import Card._
    import CardColor._
    import CardValue._
    "be one of 5 colors (Red, Blue, Green, Yellow, Black) and have a value between 0 and 9 and +2, Skip for Black the values are Wildcard and +4" in {
      val red: Array[Card] = Card.values.filter(c => c.getColor == Red)
      red.size should be(13)

      red.map(c => c.getValue).toSet shouldBe Set(
        Zero,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Take2,
        Skip,
        Special
      )
      val blue: Array[Card] = Card.values.filter(c => c.getColor == Blue)
      blue.size should be(13)
      blue.map(c => c.getValue).toSet shouldBe Set(
        Zero,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Take2,
        Skip,
        Special
      )
      val green: Array[Card] = Card.values.filter(c => c.getColor == Green)
      green.size should be(13)
      green.map(c => c.getValue).toSet shouldBe Set(
        Zero,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Take2,
        Skip,
        Special
      )
      val yellow: Array[Card] = Card.values.filter(c => c.getColor == Yellow)
      yellow.size should be(13)
      yellow.map(c => c.getValue).toSet shouldBe Set(
        Zero,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Take2,
        Skip,
        Special
      )
      val black: Array[Card] = Card.values.filter(c => c.getColor == Black)
      black.size shouldBe (2)
      black.map(c => c.getValue).toSet shouldBe Set(Wildcard, Take4)

      val error = (XX)
      error.getColor shouldBe (CardColor.ErrorC)
      error.getValue shouldBe (CardValue.Error)
    }
    "have a method getColor that returns the color of the card" in {
      R0.getColor should be(CardColor.Red)
      B0.getColor should be(CardColor.Blue)
      G0.getColor should be(CardColor.Green)
      Y0.getColor should be(CardColor.Yellow)
      W.getColor shouldBe (CardColor.Black)
    }
    "have a method getValue that returns the color of the card" in {
      R0.getValue should be(CardValue.Zero)
      B0.getValue should be(CardValue.Zero)
      G0.getValue should be(CardValue.Zero)
      Y0.getValue should be(CardValue.Zero)
      R1.getValue should be(CardValue.One)
      T4.getValue should be(CardValue.Take4)
    }
    "have its own toString Method that prints it's ID" in {
      R0.toString should be("R0")
      B0.toString should be("B0")
      G0.toString should be("G0")
      Y0.toString should be("Y0")
      W.toString should be("WC")
    }
    "have a method getCard(String) that converts a String into a card" in {
      toCard.getCard("R9") should be(R9)
      toCard.getCard("B9") should be(B9)
      toCard.getCard("G9") should be(G9)
      toCard.getCard("Y9") should be(Y9)
      toCard.getCard("WC") should be(W)
      toCard.getCard("+4") should be(T4)
      toCard.getCard("R13") should be(XX)
      toCard.getCard("Hallo") should be(XX)
    }
  }
}
