package de.htwg.se.uno
package model.gameComponent.gameBaseImpl
import Card._
import CardLayout._

case class Player(name: String, karten: Vector[Card], placed: Boolean) {

  def print(): String =
    if (karten.isEmpty) {
      udRow(1) + "|  |" + eol + udRow(1)
    } else {
      val midLine =
        karten.map(_.toString).map("" + _ + "").mkString("|", "|", "|") + eol
      udRow(karten.size) + midLine + udRow(karten.size)
    }

  def printFiller(): String =
    if (karten.size < 10) {
      udRow(1) + "| " + karten.size + "|" + eol + udRow(1)
    } else {
      udRow(1) + "|" + karten.size + "|" + eol + udRow(1)
    }

  def removeInd(ind: Int): Player =
    val (tmp1, tmp2) = karten.splitAt(ind)
    copy(name, tmp1 ++ tmp2.drop(1), true)

  def setFalse(): Player =
    copy(name, karten, false)

  def setTrue(): Player =
    copy(name, karten, true)

  def add(karte: Card): Player =
    copy(
      name,
      karten :+ karte
    )
}
