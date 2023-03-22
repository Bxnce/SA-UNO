package de.htwg.se.uno
package model.gameComponent.gameBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import CardLayout._
class CardLayoutSpec extends AnyWordSpec {
  "The Object CardLayout" should {
    "have a variable eol as String of form'\\n'" in {
      eol should be("\n")
    }
    "have a variable row as String of form '+--'" in {
      row should be("+--")
    }
    "have a variable rowEnd as String of form '+'" in {
      rowEnd should be("+" + eol)
    }
    "have a scalable method udRow(Int)" in {
      udRow(1) should be("+--+" + eol)
      udRow(2) should be("+--+--+" + eol)
      udRow(3) should be("+--+--+--+" + eol)
    }
  }
}
